/**
 * SmartStock AI — light client helpers (Bootstrap handles most UI).
 */
(function () {
    "use strict";

    document.querySelectorAll("[data-confirm]").forEach(function (el) {
        el.addEventListener("submit", function (e) {
            var msg = el.getAttribute("data-confirm");
            if (msg && !window.confirm(msg)) {
                e.preventDefault();
            }
        });
    });
})();
