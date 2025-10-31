document.addEventListener("DOMContentLoaded", () => {
  document.querySelectorAll(".btn-delete").forEach(btn => {
    btn.addEventListener("click", (e) => {
      if (!confirm("Êtes-vous sûr de vouloir supprimer ce produit ?")) {
        e.preventDefault();
      }
    });
  });
});