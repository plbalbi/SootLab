// Instantiate editor from <textarea> tag
var javaEditor = CodeMirror.fromTextArea(document.getElementById("java-code"), {
    lineNumbers: true,
    mode: "text/x-java",
    theme: "idea"
  });

// Trigger compilation mannually
javaEditor.setOption("extraKeys", {
    "Cmd-Enter": cm => {
        console.log("Called compile from Cmd+Enter");
        compile();
    }
})

// Occupy the whole left-pane
javaEditor.setSize("100%", "100%")

// TODO: Add toggle for this
// Configure throttled continious-complation
// Throttled to once every-second at most
// Just trigger in trailing-edge, that being the down edge of the 1sec interval
javaEditor.on('change', _.throttle(editor => {
    compile();
}, 1000, {
    leading: false,
    trailing: true,
}));

// Configure test-code for testing env.
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
    fetch("/api/compile", {
        method: "POST",
        body: editorContents
    })
    .catch(connectionError => {
        document.getElementById("connection_diagnostic").innerText = "Connection problems"
        console.error("There seems to be some connection problem with the backend: " + connectionError);
    })
    .then(response => response.json())
    .then(compilationResult => {
        document.getElementById("connection_diagnostic").innerText = ""
        console.log("Received the follwoing data: " + compilationResult.x + " with this diagnostics: " + compilationResult.diagnostics);
        document.getElementById("generated-output").innerText = compilationResult.x;
    })
}