console.log("this is the script file");

const toggleSidebar = () => {
  if ($(".open").is(":visible")) {
    $(".open").css("display", "none");
    $(".close").css("display", "block");
  } else {
    $(".open").css("display", "block");
    $(".close").css("display", "none");
  }
};

$(".close")
  .on("mouseenter", function () {
    toggleSidebar();
  })
  .on("mouseleave", function () {
    toggleSidebar();
  });

$(".open")
  .on("mouseenter", function () {
    $(".open").css("display", "block");
    $(".close").css("display", "none");
  })
  .on("mouseleave", function () {
    $(".open").css("display", "none");
    $(".close").css("display", "block");
  });

$("#searchInput").on("input", function () {
  const searchTerm = this.value;
  if (searchTerm != "") {
    fetch(`/search/contacts?query=${searchTerm}`)
      .then((response) => response.json())
      .then((data) => {
        displaySearchResults(data);
      })
      .catch((error) => {
        console.error("Error fetching search results:", error);
      });
  } else {
    displaySearchResults(null);
  }
});

const displaySearchResults = (results) => {
  const parent = document.getElementById("result");
  while (parent.firstChild) {
    parent.removeChild(parent.firstChild);
  }
  $(".search-result").css("display", "none");
  if (results != null) {
    $(".search-result").css("display", "block");
  }
  results.forEach((data) => {
    console.log(data);
    const result = document.getElementById("result");
    const div = document.createElement("div");
    div.classList.add("result-item");
    const img = document.createElement("img");
    img.src = data.image!= null ? "/img/" + data.image : "/img/contact.png";
    img.alt = "Image";
    const paragraph = document.createElement("p");
    paragraph.textContent = data.name;
    div.appendChild(img);
    div.appendChild(paragraph);
    
     div.addEventListener("click", () => {
       window.location = "/user/" + data.cid + "SCM/contact";
     });
    result.appendChild(div);
  });
};
