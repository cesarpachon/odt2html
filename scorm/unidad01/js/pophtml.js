// JavaScript Document

function cargarDiv(){

url="/blank.html";
new Ajax.Request(url, {
  method: 'get',
  onSuccess: function(transport) {
  $('contenido').innerHTML=transport.responseText;
  }
});
}  