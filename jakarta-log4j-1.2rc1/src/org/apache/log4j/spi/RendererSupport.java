

package org.apache.log4j.spi;

import org.apache.log4j.*;
import org.apache.log4j.or.RendererMap;
import org.apache.log4j.or.ObjectRenderer;

import java.util.Enumeration;

public interface RendererSupport {

  public
  RendererMap getRendererMap();

  public
  void setRenderer(Class renderedClass, ObjectRenderer renderer);

}
