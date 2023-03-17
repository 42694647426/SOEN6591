package ca.concordia.soen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;

class DestructiveWrapping {

  public static void main(String[]  args) {
    ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
    File file = new File(args[0]);
    List<String> files = new ArrayList<String>();
    Filewalker.listOfFiles(file, files);
    for (String filename : files) {
      String source;
      try {
        source = StaticAnalysisDemo.read(filename);
      } catch (IOException e) {
        System.err.println(e);
        continue;
      }

      parser.setSource(source.toCharArray());

      //ASTNode root = parser.createAST(null);
      final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

      Visitor visitor = new Visitor();
      cu.accept(new ASTVisitor() {
          public boolean visit(CatchClause node) {
              Statement statement = node.getBody();
              if (statement instanceof Block) {
                  Block block = (Block) statement;
                  for (Object obj : block.statements()) {
                      if (obj instanceof ThrowStatement) {
                          ThrowStatement throwStatement = (ThrowStatement) obj;
                          Expression expr = throwStatement.getExpression();
                          if (expr instanceof ClassInstanceCreation) {
                              ClassInstanceCreation instanceCreation = (ClassInstanceCreation) expr;
                              if (instanceCreation.arguments().isEmpty()) {
                                  System.out.println("Possible destructive wrapping found at line: "
                                          + cu.getLineNumber(node.getStartPosition()));
                              }
                          }
                      }
                  }
              }
              return super.visit(node);
          }
      });

      //System.out.println(filename + ": " + visitor.count);

      for (String name : visitor.names) {
        System.out.println(name);
      }
    }
  }


  static class Visitor extends ASTVisitor {
    int count = 0;
    List<String> names = new ArrayList<>();

    @Override
    public boolean visit(CatchClause clause) {
      count += 1;
      names.add(clause.getException().toString());
      return false;
    }
  }

}
