package ca.concordia.soen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;


public class StaticAnalysisDemo 
{
  public static void main( String[] args )
  {
    ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
    
    for (String filename : args) {
      String source;
      try {
        source = read(filename); 
      } catch (IOException e) {
        System.err.println("can not read file: " + filename);
        continue;
      }

      parser.setSource(source.toCharArray());

      ASTNode root = parser.createAST(null);
      
      System.out.println(tree(root));
    }
  }


  public static String read(String filename) throws IOException {
    Path path = Paths.get(filename);

    String source = Files.lines(path).collect(Collectors.joining("\n"));

    return source;
  }

  private static String tree(ASTNode root) {
      TreeBuilder builder = new TreeBuilder();
      root.accept(builder);
      return builder.tree(root);
  } 

  private static class TreeBuilder extends ASTVisitor {
    // mapping from parent to child
    Map<ASTNode, ASTNode> child = new HashMap<>();

    // mapping from node to next sibling
    Map<ASTNode, ASTNode> sibling = new HashMap<>();

    // last visited node
    ASTNode last = null;

    // stack of currently visiting nodes
    Stack<ASTNode> stack = new Stack<>();

    public ASTNode parent(ASTNode node) {
      return node.getParent();
    }

    public ASTNode child(ASTNode node) {
      return child.get(node);
    }

    public ASTNode sibling(ASTNode node) {
      return sibling.get(node);
    }

    @Override
    public void preVisit(ASTNode node) {
      ASTNode parent = node.getParent();

      stack.push(node);

      if (parent != null && child(parent) == null) {
        child.put(parent, node);
      } 

      if (last != null && parent(last) == parent(node)) {
        sibling.put(last, node);
      }
    }

    @Override
    public void postVisit(ASTNode node) {
      last = stack.pop();
    }

    public String repr(ASTNode node) {
      return node.getClass().getSimpleName();
    }

    public String tree(ASTNode root) {
      StringBuilder builder = new StringBuilder();
      builder.append(repr(root));
      builder.append(System.lineSeparator());
      builder.append(tree(child(root), ""));
      return builder.toString();
    }

    public String tree(ASTNode node, String prefix) {
      if (node == null) {
        return "";
      }

      StringBuilder builder = new StringBuilder();
      builder.append(prefix);
      if (sibling(node) != null) {
        builder.append("├─");
        builder.append(repr(node));
        builder.append(System.lineSeparator());
        builder.append(tree(child(node), prefix + "│ "));
        builder.append(tree(sibling(node), prefix));
      } else {
        builder.append("└─");
        builder.append(repr(node));
        builder.append(System.lineSeparator());
        builder.append(tree(child(node), prefix + "  "));
      }

      return builder.toString();
    }
  }
}
