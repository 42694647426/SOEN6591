package ca.concordia.soen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;

public class Example2 {

  public static void main(String[] args) {
    System.out.println("Hello World!");
  }

  public void test() {
    Function method = new Function() {
      @Override
      public Object apply(Object arg0) {
        return null;
      }
      public void test3() {
    	  try {
    		  throw new Exception("asdf");
    	  } catch(Exception e2) {
    		  
    	  }
      }
    };
  }

  public void test2() {

    try {
      Files.lines(Paths.get("asdfa"));
    } catch (IOException e) {
      try {
        throw new Exception("asdf");
      } catch (Exception e1) {

      }
    }
    
  };

}

