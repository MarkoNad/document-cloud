$(function () { // Same as document.addEventListener("DOMContentLoaded"...

  // Same as document.querySelector("#navbarToggle").addEventListener("blur",...
  $("#navbarToggle").blur(function (event) {
    var screenWidth = window.innerWidth;
    if (screenWidth < 768) {
      $("#collapsable-nav").collapse('hide');
    }
  });

  // In Firefox and Safari, the click event doesn't retain the focus
  // on the clicked button. Therefore, the blur event will not fire on
  // user clicking somewhere else in the page and the blur event handler
  // which is set up above will not be called.
  // Refer to issue #28 in the repo.
  // Solution: force focus on the element that the click event fired on
  $("#navbarToggle").click(function (event) {
    $(event.target).focus();
  });
});

(function (global) {

var dc = {};

var homeHtml = "snippets/home-snippet.html";
var allCategoriesUrl = "https://davids-restaurant.herokuapp.com/categories.json";
var categoriesTitleHtml = "snippets/categories-title-snippet.html";
var categoryHtml = "snippets/category-snippet.html";
var menuItemsUrl = "https://davids-restaurant.herokuapp.com/menu_items.json?category=";
var menuItemsTitleHtml = "snippets/menu-items-title.html";
var menuItemHtml = "snippets/menu-item.html";

// Convenience function for inserting innerHTML for 'select'
var insertHtml = function (selector, html) {
  var targetElem = document.querySelector(selector);
  targetElem.innerHTML = html;
};

// Show loading icon inside element identified by 'selector'.
var showLoading = function (selector) {
  var html = "<div class='text-center'>";
  html += "<img src='images/ajax-loader.gif'></div>";
  insertHtml(selector, html);
};

// Return substitute of '{{propName}}'
// with propValue in given 'string'
var insertProperty = function (string, propName, propValue) {
  var propToReplace = "{{" + propName + "}}";
  string = string
    .replace(new RegExp(propToReplace, "g"), propValue);
  return string;
}

// Remove the class 'active' from home and switch to Menu button
var switchMenuToActive = function () {
  // Remove 'active' from home button
  var classes = document.querySelector("#navHomeButton").className;
  classes = classes.replace(new RegExp("active", "g"), "");
  document.querySelector("#navHomeButton").className = classes;

  // Add 'active' to menu button if not already there
  classes = document.querySelector("#navMenuButton").className;
  if (classes.indexOf("active") == -1) {
    classes += " active";
    document.querySelector("#navMenuButton").className = classes;
  }
};

// On page load (before images or CSS)
document.addEventListener("DOMContentLoaded", function (event) {

// On first load, show home view
showLoading("#main-content");
$ajaxUtils.sendGetRequest(
  homeHtml,
  function (responseText) {
    document.querySelector("#main-content").innerHTML = responseText;
    // addUploadListener();
  },
  false);
});

function addUploadListener() {
  const form = document.querySelector('#uploadForm');

  console.log("Adding event listener to upload form: ", form);
  form.addEventListener('submit', (e) => {
    console.log("Submitting.");
    e.preventDefault()

    const files = document.querySelector('#filesInput').files
    const formData = new FormData()

    for (let i = 0; i < files.length; i++) {
      let file = files[i]
      console.log("file " + i + ": " + file);

      formData.append('files', file)
      // formData.append(file.name, file);
    }

    fetch(fileUploadUrl, {
      method: 'POST',
      body: formData,
    }).then((response) => {
      console.log(response)
    })
  });
}








dc.uploadFile = function() {
  console.log("Uploading file.");

  // $ajaxUtils.sendGetRequest(
  //   fileUploadUrl,
  //   buildAndShowCategoriesHTML
  // );

  // console.log("Uploaded.");

  let customFileForm = document.getElementById("customFile")
  let file = document.getElementById("customFile").files[0];
  console.log("Custom file form: ", customFileForm);
  let formData = new FormData();

  console.log("file: " + file);

  formData.append("file", file);
  fetch(fileUploadUrl, {method: "POST", body: formData});
};




const DIRECTORY_DELIMITER = "/";

var workingDirectory = "home";
changeDirectory(workingDirectory);

function changeDirectory(directory) {
    setWorkingDirectory(directory);
    showPath(directory);
    showWorkingDirFileDetails();
}

function setWorkingDirectory(path) {
    console.log("Setting workingDirectory to: ", path);
    localStorage.setItem('workingDirectory', path);
}

function showPath(path) {
    var contents = "";

    var folders = path.split(DIRECTORY_DELIMITER);
    for (var i = 0; i < folders.length; i++) {
        if (i != 0) {
            contents += " > ";
        }
        var folder = folders[i];
        contents += "<a>" + folder + "</a>";
    }

    var workingDirectoryDiv = document.getElementById('working-directory');
    workingDirectoryDiv.innerHTML = path;
}

function showWorkingDirFileDetails() {
    console.log("Fetching working directory details.");

    var workingDirectory = localStorage.getItem('workingDirectory');
    console.log("Working directory: ", workingDirectory);

    $ajaxUtils.sendGetRequest(
        detailsUrl + workingDirectory,
        renderWorkingDirFileDetails,
        true
    );

    function renderWorkingDirFileDetails(detailsArray) {
        console.log("Response JSON: ", detailsArray);

        var contents = "";

        for (var i = 0; i < detailsArray.length; i++) {
            var details = detailsArray[i];

            var name = details.name;
            var type = details.type;

            console.log("name: ", name);
            console.log("type: ", type);

            contents += "<hr>";
            contents += "<p>Name: " + name + ", type: " + type;
        }

        contents += "<hr>";

        var contentsDiv = document.getElementById('working-directory-contents');
        contentsDiv.innerHTML = contents;
    }
}



let filesUploaded = 0;

let directoryPicker = document.getElementById('directory-picker');
directoryPicker.addEventListener('change', e => {
    showProgressTile();

    let total = directoryPicker.files.length;
    filesUploaded = 0;

    displayUploadAnimation();

    for (var i = 0; i < directoryPicker.files.length; i++) {
        var file = directoryPicker.files[i];
        sendFile(file, total);
    }
});

let filePicker = document.getElementById('file-picker');
filePicker.addEventListener('change', e => {
    showProgressTile();

    let total = filePicker.files.length;
    filesUploaded = 0;

    displayUploadAnimation();

    for (var i = 0; i < filePicker.files.length; i++) {
        var file = filePicker.files[i];
        sendFile(file, total);
    }
});

function showProgressTile() {
    let progressTile = document.getElementById("progress-tile");
    progressTile.style.display = "block";
    progressTile.style.visibility = "visible";
}

function hideProgressTile() {
    let progressTile = document.getElementById("progress-tile");
    progressTile.style.display = "none";
    progressTile.style.visibility = "hidden";
}

function hideUploadAnimation() {
    let loader = document.getElementById("loader");
    loader.style.display = "none";
    loader.style.visibility = "hidden";
}

function displayUploadAnimation() {
    let loader = document.getElementById("loader");
    loader.style.display = "block";
    loader.style.visibility = "visible";
}

function showProgress(filesUploaded, total) {
    var percentage = Math.min(filesUploaded / total * 100, 100).toFixed(2) + "%";
    let progress = document.getElementById('progress');
    progress.innerHTML = "Uploaded " + filesUploaded + " of " + total + " files (" + percentage + ").";
}

function showDialog(text, duration) {
    let dialog = document.getElementById('dialog');
    dialog.innerHTML = text;
    $(function() {
        $("#dialog").dialog();
    });

    setTimeout(
        function() {
            $("#dialog").dialog("close");
        },
        duration
    );
}

sendFile = function sendfile(file, total) {
    var request = new XMLHttpRequest();

    request.responseType = 'text';

    request.onload = function() {
        if (request.readyState !== request.DONE) {
            return;
        }

        if (request.status === 200) {
            filesUploaded++;
            showProgress(filesUploaded, total);
        }

        if (filesUploaded >= total) {
            showDialog("Uploading " + total + " file(s) done!", 4000)
            progress.innerHTML = "";
            hideProgressTile();
            resetPickers();
        }
    };

    var formData = new FormData();
    var relativePath = file.webkitRelativePath ? file.webkitRelativePath : file.name;
    var path = localStorage.getItem('workingDirectory') + DIRECTORY_DELIMITER + relativePath;
    formData.set('file', file);
    formData.set('absolute-path', path);

    console.log("path: " , path);

    request.open("POST", fileUploadUrl);
    request.send(formData);
};

function resetPickers() {
    directoryPicker.value = "";
    filePicker.value = "";
}


global.$dc = dc;

})(window);
