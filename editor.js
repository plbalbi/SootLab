var javaEditor = CodeMirror.fromTextArea(document.getElementById("java-code"), {
    lineNumbers: true,
    mode: "text/x-java",
    value: `
package test;
public class A {
	public static void main(String[] args) {
		int i = 0;
		i++;
	}
}`
  });

function compile() {
    var editorContents = javaEditor.getValue();
    fetch("http://localhost:8080/compile", {
        method: "POST",
        body: editorContents
    })
    .then(data => console.log("Received the follwoing data: " + data))

}