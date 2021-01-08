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

var directoryHtml = "snippets/directory-snippet.html";

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

function getWorkingDirectory() {
    return localStorage.getItem('workingDirectory');    
}

function showPath(path) {
    var contents = "";

    var directories = path.split(DIRECTORY_DELIMITER);
    var absolutePath = "";
    for (var i = 0; i < directories.length; i++) {
        if (i != 0) {
            contents += "<span> > </span>";
            absolutePath += DIRECTORY_DELIMITER;
        }

        var directoryName = directories[i];
        absolutePath += directoryName;

        contents += "<a onclick='$dc.changeDirectory(\"" + absolutePath + "\")'>" + directoryName + "</a>";
    }

    var workingDirectoryDiv = document.getElementById('working-directory');
    workingDirectoryDiv.innerHTML = contents;
}

function showWorkingDirFileDetails() {
    console.log("Fetching working directory details.");

    var workingDirectory = getWorkingDirectory();
    console.log("Working directory: ", workingDirectory);

    $ajaxUtils.sendGetRequest(
        encodeURI(detailsUrl + "?directory=" + workingDirectory),
        renderWorkingDirFileDetails,
        true
    );

    function renderWorkingDirFileDetails(detailsArray) {
        console.log("Response JSON: ", detailsArray);

        var fileDetails = [];
        var directoryDetails = [];

        for (var i = 0; i < detailsArray.length; i++) {
            var details = detailsArray[i];

            var name = details.name;
            var type = details.type;

            if (type == "DIRECTORY") {
                directoryDetails.push(details);
            } else if (type == "FILE") {
                fileDetails.push(details);
            }

            console.log("name: ", name);
            console.log("type: ", type);
        }

        var contents = "";

        for (var i = 0; i < directoryDetails.length; i++) {
            contents += "<hr>";
            var directoryName = directoryDetails[i].name;
            console.log("directory name: ", directoryName);

            var directoryNode = populateDirectoryTemplate(directoryName);
            console.log("Directory node: ", directoryNode);
            contents += directoryNode;
        }

        dc.images = [];

        for (var i = 0; i < fileDetails.length; i++) {
            var file = fileDetails[i];

            if (isImage(file.name)) {
                var downloadUrl = encodeURI(getFileUrl + "?file=" + getWorkingDirectory() + DIRECTORY_DELIMITER + file.name) ;
                dc.images.push(downloadUrl);
            }

            contents += "<hr>";

            var fileNode = populateFileTemplate(file);
            console.log("File node: ", fileNode);
            contents += fileNode;
        }

        console.log("Images: ", dc.images);

        contents += "<hr>";

        var contentsDiv = document.getElementById('working-directory-contents');
        contentsDiv.innerHTML = contents;
    }
}

function isImage(fileName) {
    var extension = getFileExtension(fileName);
    if (extension == null) {
        return false;
    }
    return isImageExtension(extension);
}

function isImageExtension(extension) {
    return extension.match(/(jpg|jpeg|png|gif)$/i);
}

function populateDirectoryTemplate(directoryName) {
    var absolutePath = getWorkingDirectory() + DIRECTORY_DELIMITER + directoryName;
    var downloadUrl = encodeURI(getDirectoryUrl + "?directory=" + absolutePath);
    return "<div onclick=\"$dc.changeDirectory('" + absolutePath + "')\">" +
            "<i class=\"material-icons\">folder2</i>" +
            "<span>" + directoryName + "</span>" +
            "<a class=\"material-icons\" href=\"" + downloadUrl + "\">download</a>" +
        "</div>";
}

function populateFileTemplate(file) {
    console.log("file name: ", file.name);
    var absolutePath = getWorkingDirectory() + DIRECTORY_DELIMITER + file.name;
    var downloadUrl = encodeURI(getFileUrl + "?file=" + absolutePath);

    return "<div onclick=\"$dc.viewFile('" + absolutePath + "')\">" +
            "<span class=\"glyphicon glyphicon-file\"></span>" +
            "<span>" + file.name + "</span>" +
            "<a class=\"material-icons\" href=\"" + downloadUrl + "\">download</a>" +
        "</div>";
}

let filesUploaded = 0;

let directoryPicker = document.getElementById('directory-picker');
directoryPicker.addEventListener('change', e => {
    uploadDirectory();
});

function uploadDirectory() {
    showProgressTile();

    let total = directoryPicker.files.length;
    filesUploaded = 0;

    displayUploadAnimation();

    for (var i = 0; i < directoryPicker.files.length; i++) {
        var file = directoryPicker.files[i];
        sendFile(file, total);
    }
}

let filePicker = document.getElementById('file-picker');
filePicker.addEventListener('change', e => {
    uploadFiles();
});

function uploadFiles() {
    showProgressTile();

    let total = filePicker.files.length;
    filesUploaded = 0;

    displayUploadAnimation();

    for (var i = 0; i < filePicker.files.length; i++) {
        var file = filePicker.files[i];
        sendFile(file, total);
    }
}

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
            showWorkingDirFileDetails();
        }
    };

    var formData = new FormData();
    var relativePath = file.webkitRelativePath ? file.webkitRelativePath : file.name;
    var path = getWorkingDirectory() + DIRECTORY_DELIMITER + relativePath;
    formData.set('file', file);
    formData.set('absolute-path', path);
    formData.set('last-modified', file.lastModified);

    console.log("path: " , path);
    console.log("modified: " + file.lastModified);

    request.open("POST", fileUploadUrl);
    request.send(formData);
};

function resetPickers() {
    directoryPicker.value = "";
    filePicker.value = "";
}

dc.createDirectory = function() {
    var newDirectory = prompt("Enter new directory name.");
    if (newDirectory == null || newDirectory == "") {
        return;
    }

    var request = new XMLHttpRequest();

    request.responseType = 'text';

    request.onload = function() {
        if (request.readyState !== request.DONE) {
            return;
        }

        if (request.status === 200) {
            showWorkingDirFileDetails();
        }
    };

    var formData = new FormData();
    var absolutePath = getWorkingDirectory() + DIRECTORY_DELIMITER + newDirectory;
    formData.set('directory', absolutePath);

    request.open("POST", encodeURI(createDirectoryUrl));
    request.send(formData);
}

dc.viewFile = function(absolutePath) {
    var preview = document.getElementById('preview');

    var extension = getFileExtension(absolutePath);
    if (extension == null || !isImageExtension(extension)) {
        preview.innerHTML = "<p>No preview available for " + extension + " files.</p>";
        return;
    }

    var src = encodeURI(getFileUrl + "?file=" + absolutePath);
    var imageTag = "<img src='" + src + "' alt='Preview'>";

//    preview.innerHTML = imageTag;

    var slide = document.querySelector('#slide');
    slide.src = src;
}

dc.nextSlide = function() {
    incrementSlidePointer(1);
}

dc.previousSlide = function() {
    incrementSlidePointer(-1);
}

function incrementSlidePointer(count) {
    var slide = document.querySelector('#slide');
    var currentImageUrl = slide.src;
    var indexOfCurrent = dc.images.indexOf(currentImageUrl);
    var nextIndex = (indexOfCurrent + count) % dc.images.length;
    if (nextIndex < 0) {
        nextIndex += dc.images.length;
    }
    var nextImageUrl = dc.images[nextIndex];
    console.log("Viewing next image: ", nextImageUrl);
    slide.src = nextImageUrl;
}

function getFileExtension(absolutePath) {
    var parts = absolutePath.split(".");
    if (parts.length === 1 || (parts[0] === "" && parts.length === 2)) {
        return null;
    }
    return parts.pop();
}

dc.performClick = function(elemId) {
   var elem = document.getElementById(elemId);
   if(elem && document.createEvent) {
      var evt = document.createEvent("MouseEvents");
      evt.initEvent("click", true, false);
      elem.dispatchEvent(evt);
   }
}

dc.download = function(d) {
    if (d == 'Select document') {
        return;
    }
    window.location = 'https://document-cloud.hr/documentcloud/get-file?file=' + d;
}

dc.changeDirectory = changeDirectory;
dc.uploadDirectory = uploadDirectory;
dc.uploadFiles = uploadFiles;

global.$dc = dc;

})(window);
