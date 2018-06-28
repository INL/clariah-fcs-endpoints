// Get the input field
var queryField = document.getElementById("queryField");

// Execute a function when the user releases a key on the keyboard
queryField.addEventListener("keyup", function(event) {
  // Cancel the default action, if needed
  event.preventDefault();
  // Number 13 is the "Enter" key on the keyboard
  if (event.keyCode === 13) {
	  // Trigger the button element with a click
	  document.getElementById("searchButton").click();
  }
});

function doQuery() {
    var query = queryField.value;
    var corpus = corpusField.value;
    var getUrl = window.location;
    var baseUrl = getUrl .protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1];
    var urlTemplate = "/sru?operation=searchRetrieve&queryType=fcs&x-fcs-context=" + corpus + "&maximumRecords=20&query=";
    var queryUrl = baseUrl + urlTemplate+encodeURIComponent(query);
    window.open(queryUrl,'_blank');
}