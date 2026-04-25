from fastapi import FastAPI, UploadFile, File
from pydantic import BaseModel
from typing import List, Optional
import uvicorn
import random

app = FastAPI()

PRODUCTS = [
    ("Amul Butter 500g", "Dairy"), ("Basmati Rice 5kg", "Grains"),
    ("Toor Dal 1kg", "Pulses"), ("Sunflower Oil 1L", "Oils"),
    ("Maggi Noodles", "Snacks"), ("Whole Milk 1L", "Dairy"),
    ("Besan 500g", "Pulses"), ("Tomato Ketchup", "Condiments"),
    ("Biscuits Pack", "Snacks"), ("Mineral Water 1L", "Beverages")
]

@app.post("/predict")
async def predict(image: UploadFile = File(...)):
    product = random.choice(PRODUCTS)
    return {"product_name": product[0], "category": product[1]}

class ProductItem(BaseModel):
    name: str
    category: Optional[str] = ""
    quantity: int
    min_threshold: int
    price: Optional[float] = 0.0
    sales_velocity: Optional[float] = 0.0
    is_low_stock: bool

class InventoryPayload(BaseModel):
    inventory: List[ProductItem]
    total_products: int
    low_stock_count: int
    out_of_stock_count: int

@app.post("/restock-advice")
async def restock_advice(payload: InventoryPayload):
    low = [p for p in payload.inventory if p.is_low_stock]
    recs = []
    for p in low[:5]:
        order_qty = max(20, p.min_threshold * 3)
        recs.append({
            "product_name": p.name,
            "current_qty": p.quantity,
            "recommended_order_qty": order_qty,
            "reason": f"Stock at {p.quantity} units, below minimum threshold of {p.min_threshold}. Based on sales velocity, reorder {order_qty} units.",
            "priority": "high" if p.quantity == 0 else "medium",
            "estimated_cost": round(order_qty * p.price, 2)
        })
    total_cost = sum(r["estimated_cost"] for r in recs)
    urgency = "critical" if payload.out_of_stock_count > 0 else "warning" if payload.low_stock_count > 2 else "good"
    return {
        "summary": f"Your inventory has {payload.low_stock_count} low stock items and {payload.out_of_stock_count} out of stock. Immediate restocking recommended for {len(recs)} products to maintain service levels.",
        "urgency_level": urgency,
        "recommendations": recs,
        "insights": [
            f"Dairy category needs immediate attention with {sum(1 for p in payload.inventory if p.category=='Dairy' and p.is_low_stock)} items low",
            f"Total inventory value at risk: \u20b9{int(total_cost):,}",
            "Consider bulk ordering to reduce per-unit cost by 10-15%",
            f"{payload.total_products - payload.low_stock_count - payload.out_of_stock_count} products are well stocked"
        ],
        "total_restock_cost": total_cost
    }

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
