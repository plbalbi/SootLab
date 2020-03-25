var javaEditor = CodeMirror.fromTextArea(document.getElementById("java-code"), {
    lineNumbers: true,
    mode: "text/x-java",
    theme: "idea"
  });
javaEditor.setOption("extraKeys", {
    "Cmd-Enter": cm => {
        console.log("Called compile from Cmd+Enter");
        compile();
    }
})
javaEditor.setSize("100%", "100%")

developBootstrap();

function developBootstrap() {
    javaEditor.setValue(`package test;
    public class A {
        public static void main(String[] args) {
            int i = 0;
            i++;
        }
    }
    `);
    compile();
}

function compile() {
    var editorContents = javaEditor.getValue();
    fetch("http://localhost:8080/compile", {
        method: "POST",
        body: editorContents
    })
    .then(response => response.json())
    .then(compilationResult => {
        console.log("Received the follwoing data: " + compilationResult.x + " with this diagnostics: " + compilationResult.diagnostics);
        document.getElementById("generated-output").innerText = compilationResult.x;
    })

}