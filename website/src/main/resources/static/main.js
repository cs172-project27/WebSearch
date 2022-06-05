function getSearchResults(){
    var form = document.getElementById('search_form');
    form.action = "http://localhost:8080/results/";
}