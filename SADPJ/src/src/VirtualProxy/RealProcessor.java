package src.VirtualProxy;
public class RealProcessor extends IDEOperation {
  JavaDoc jdoc;
  public RealProcessor() {
    super();
    jdoc = new JavaDoc();
  }
  public void generateDocs(String javaFile) {
    jdoc.generateDocs(javaFile);
  }
}
class JavaDoc {

  public void generateDocs(String javaFile) {
    //
  }
}
