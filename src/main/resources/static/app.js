const activeNav = () => {
  const items = document.querySelectorAll(".nav-item");
  items.forEach((item) => {
    item.addEventListener("click", () => {
      items.forEach((el) => el.classList.remove("active"));
      item.classList.add("active");
    });
  });
};

const setDateLabel = () => {
  const heading = document.querySelector(".hero-panel h2");
  if (!heading) return;

  const now = new Date();
  const formatted = now.toLocaleDateString("pt-BR", {
    weekday: "long",
    day: "2-digit",
    month: "long",
  });

  heading.textContent = formatted.charAt(0).toUpperCase() + formatted.slice(1);
};

activeNav();
setDateLabel();
