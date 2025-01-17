package org.apache.velocity.runtime;

/*
 * Copyright 2000-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.velocity.Template;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.directive.Macro;
import org.apache.velocity.runtime.directive.VelocimacroProxy;
import org.apache.velocity.runtime.log.Log;

import java.util.Vector;
import java.util.Map;
import java.util.HashMap;

/**
 *  VelocimacroFactory.java
 *
 *   manages the set of VMs in a running Velocity engine.
 *
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @version $Id: VelocimacroFactory.java 383711 2006-03-07 00:01:00Z nbubna $ 
 */
public class VelocimacroFactory
{
    /**
     *  runtime services for this instance
     */
    private RuntimeServices rsvc = null;

    /** 
     *  the log for this instance
     */
    private Log log = null;

    /**
     *  VMManager : deal with namespace management
     *  and actually keeps all the VM definitions
     */
    private VelocimacroManager vmManager = null;

    /**
     *  determines if replacement of global VMs are allowed
     *  controlled by  VM_PERM_ALLOW_INLINE_REPLACE_GLOBAL
     */
    private boolean replaceAllowed = false;

    /**
     *  controls if new VMs can be added.  Set by
     *  VM_PERM_ALLOW_INLINE  Note the assumption that only
     *  through inline defs can this happen.
     *  additions through autoloaded VMs is allowed
     */
    private boolean addNewAllowed = true;

    /**
     *  sets if template-local namespace in used
     */
    private boolean templateLocal = false;

    /**
     *  controls log output
     */
    private boolean outputMessages = false;

    /**
     *  determines if the libraries are auto-loaded
     *  when they change
     */
    private boolean autoReloadLibrary = false;

    /**
     *  vector of the library names
     */
    private Vector macroLibVec = null;

    /**
     *  map of the library Template objects
     *  used for reload determination
     */
    private Map libModMap;

    /**
     *  CTOR : requires a runtime services from now
     *  on
     */
    public VelocimacroFactory( RuntimeServices rs )
    {
        this.rsvc = rs;
        this.log = rs.getLog();

        /*
         *  we always access in a synchronized(), so we 
         *  can use an unsynchronized hashmap
         */
        libModMap = new HashMap();
        vmManager = new VelocimacroManager( rsvc );
    }

    /**
     *  initialize the factory - setup all permissions
     *  load all global libraries.
     */
    public void initVelocimacro()
    {
        /*
         *  maybe I'm just paranoid...
         */
        synchronized( this )
        {
            /*
             *  respect message switch even from the start
             */
            outputMessages = rsvc.getBoolean(RuntimeConstants.VM_MESSAGES_ON, true);
            if (outputMessages && log.isTraceEnabled())
            {
                log.trace("Velocimacro : initialization starting.");
            }

            /*
             *   allow replacements while we add the libraries, if exist
             */
            setReplacementPermission( true );
 
            /*
             *  add all library macros to the global namespace
             */
            
            vmManager.setNamespaceUsage( false );
        
            /*
             *  now, if there is a global or local libraries specified, use them.
             *  All we have to do is get the template. The template will be parsed;
             *  VM's  are added during the parse phase
             */

             Object libfiles = rsvc.getProperty( RuntimeConstants.VM_LIBRARY );

             if (libfiles == null)
             {
                 debug("\"" + RuntimeConstants.VM_LIBRARY +
                     "\" is not set.  Trying default library: " + 
                     RuntimeConstants.VM_LIBRARY_DEFAULT);

                 // try the default library.
                 if (rsvc.getLoaderNameForResource(RuntimeConstants.VM_LIBRARY_DEFAULT) != null)
                 {
                     libfiles = RuntimeConstants.VM_LIBRARY_DEFAULT;
                 }
                 else
                 {
                     debug("Default library not found.");
                 }
             }

             if( libfiles != null)
             {         
                 if (libfiles instanceof Vector)
                 {
                     macroLibVec = (Vector) libfiles;
                 }
                 else if (libfiles instanceof String)
                 { 
                     macroLibVec = new Vector();
                     macroLibVec.addElement( libfiles );
                 }
                 
                 for( int i = 0; i < macroLibVec.size(); i++)
                 {
                     String lib = (String) macroLibVec.elementAt(i);
                 
                     /*
                      * only if it's a non-empty string do we bother
                      */

                     if (lib != null && !lib.equals(""))
                     {
                         /*
                          *  let the VMManager know that the following is coming
                          *  from libraries - need to know for auto-load
                          */

                         vmManager.setRegisterFromLib( true );

                         debug("adding VMs from VM library : " + lib);

                         try 
                         {
                             Template template = rsvc.getTemplate( lib );

                             /*
                              *  save the template.  This depends on the assumption
                              *  that the Template object won't change - currently
                              *  this is how the Resource manager works
                              */

                             Twonk twonk = new Twonk();
                             twonk.template = template;
                             twonk.modificationTime = template.getLastModified();
                             libModMap.put( lib, twonk );                         
                         } 
                         catch (Exception e)
                         {
                             log.error("Velocimacro : Error using VM library : "+lib, e);
                         }

                         if (outputMessages && log.isTraceEnabled())
                         {
                             log.trace("Velocimacro : VM library registration complete.");
                         }
            
                         vmManager.setRegisterFromLib( false );
                     }
                 }
             }

            /*
             *   now, the permissions
             */

            
            /*
             *  allowinline : anything after this will be an inline macro, I think
             *  there is the question if a #include is an inline, and I think so
             *
             *  default = true
             */
            setAddMacroPermission( true );
                        
            if ( !rsvc.getBoolean(  RuntimeConstants.VM_PERM_ALLOW_INLINE, true) )
            {
                setAddMacroPermission( false );
                
                info("allowInline = false : VMs can NOT be defined inline in templates");
            }
            else
            {
                debug("allowInline = true : VMs can be defined inline in templates");
            }

            /*
             *  allowInlineToReplaceGlobal : allows an inline VM , if allowed at all,
             *  to replace an existing global VM
             *
             *  default = false
             */
            setReplacementPermission( false );
            
            if ( rsvc.getBoolean(  
                 RuntimeConstants.VM_PERM_ALLOW_INLINE_REPLACE_GLOBAL, false) )
            {
                setReplacementPermission( true );
                
                info("allowInlineToOverride = true : VMs " +
                    "defined inline may replace previous VM definitions");
            }
            else
            {
                debug("allowInlineToOverride = false : VMs " +
                    "defined inline may NOT replace previous VM definitions");
            }

            /*
             * now turn on namespace handling as far as permissions allow in the 
             * manager, and also set it here for gating purposes
             */
            vmManager.setNamespaceUsage( true );

            /*
             *  template-local inline VM mode : default is off
             */
            setTemplateLocalInline( rsvc.getBoolean(
                RuntimeConstants.VM_PERM_INLINE_LOCAL, false) );
        
            if ( getTemplateLocalInline() )
            {
                info("allowInlineLocal = true : VMs " +
                    "defined inline will be local to their defining template only.");
            }
            else
            {
                debug("allowInlineLocal = false : VMs " +
                    "defined inline will be global in scope if allowed.");
            }
 
            vmManager.setTemplateLocalInlineVM( getTemplateLocalInline() );

            /*
             *  autoload VM libraries
             */
            setAutoload( rsvc.getBoolean( RuntimeConstants.VM_LIBRARY_AUTORELOAD, false ));
        
            if (getAutoload())
            {
                info("autoload on : VM system " +
                     "will automatically reload global library macros");
            }
            else
            {
                debug("autoload off : VM system " +
                      "will not automatically reload global library macros");
            }

            if (outputMessages && log.isTraceEnabled())
            {
                log.trace("Velocimacro : initialization complete.");
            }
        }
    
        return;
    }

    /**
     *  adds a macro to the factory. 
     */
    public boolean addVelocimacro( String name, String macroBody,  
    	String argArray[], String sourceTemplate )
    {
        /*
         * maybe we should throw an exception, maybe just tell 
         * the caller like this...
         * 
         * I hate this : maybe exceptions are in order here...
         */
        if ( name == null ||   macroBody == null || argArray == null || 
        	sourceTemplate == null )
        {
            warn("VM addition rejected : programmer error : arg null");
            return false;
        }
        
        /*
         *  see if the current ruleset allows this addition
         */

        if (!canAddVelocimacro( name, sourceTemplate ))
        {
            return false;
        }

        /*
         *  seems like all is good.  Lets do it.
         */
        synchronized( this ) 
        {
            vmManager.addVM( name, macroBody, argArray, sourceTemplate );
        }

        /*
         * Report addition of the new Velocimacro.
         */
        if (outputMessages && log.isInfoEnabled())
        {
            StringBuffer msg = new StringBuffer("Velocimacro : added ");
            Macro.macroToString(msg, argArray);
            msg.append(" : source = ").append(sourceTemplate);
            log.info(msg);
        }

        return true;
    }

    /**
     *  determines if a given macro/namespace (name, source) combo is allowed
     *  to be added
     *
     *  @param name Name of VM to add
     *  @param sourceTemplate Source template that contains the defintion of the VM
     *  @return true if it is allowed to be added, false otherwise
     */
    private synchronized boolean canAddVelocimacro( String name, String sourceTemplate)
    {
        /*
         *  short circuit and do it if autoloader is on, and the
         *  template is one of the library templates
         */
        
        if ( getAutoload() && (macroLibVec != null) )
        {
            /*
             *  see if this is a library template
             */

            for( int i = 0; i < macroLibVec.size(); i++)
            {
                String lib = (String) macroLibVec.elementAt(i);

                if (lib.equals( sourceTemplate ) )
                {
                    return true;
                }
            }
        }

           
        /*
         * maybe the rules should be in manager?  I dunno. It's to manage 
         * the namespace issues first, are we allowed to add VMs at all? 
         * This trumps all.
         */
        if (!addNewAllowed)
        {
            warn("VM addition rejected : "+name+" : inline VMs not allowed.");
            return false;
        }

        /*
         *  are they local in scope?  Then it is ok to add.
         */
        if (!templateLocal)
        {
            /* 
             * otherwise, if we have it already in global namespace, and they can't replace
             * since local templates are not allowed, the global namespace is implied.
             *  remember, we don't know anything about namespace managment here, so lets
             *  note do anything fancy like trying to give it the global namespace here
             *
             *  so if we have it, and we aren't allowed to replace, bail
             */
            if ( isVelocimacro( name, sourceTemplate ) && !replaceAllowed )
            {
                warn("VM addition rejected : "+name+" : inline not allowed to replace existing VM");
                return false;
            }
        }
        
        return true;
    }

    /**
     *  localization of the logging logic
     */
    private void debug(String s)
    {
        if (outputMessages && log.isDebugEnabled())
        {
            log.debug("Velocimacro : " + s);
        }
    }

    /**
     *  localization of the logging logic
     */
    private void info(String s)
    {
        if (outputMessages && log.isInfoEnabled())
        {
            log.info("Velocimacro : " + s);
        }
    }

    /**
     *  localization of the logging logic
     */
    private void warn(String s)
    {
        if (outputMessages && log.isWarnEnabled())
        {
            log.warn("Velocimacro : " + s);
        }
    }
      
    /**
     *  Tells the world if a given directive string is a Velocimacro
     */
    public boolean isVelocimacro( String vm , String sourceTemplate )
    {
        synchronized(this)
        {
            /*
             * first we check the locals to see if we have 
             * a local definition for this template
             */
            if (vmManager.get( vm, sourceTemplate ) != null)
                return true;
        }
        return false;
    }

    /**
     *  actual factory : creates a Directive that will
     *  behave correctly wrt getting the framework to 
     *  dig out the correct # of args
     */
    public Directive getVelocimacro( String vmName, String sourceTemplate )
    {
        VelocimacroProxy vp = null;

        synchronized( this ) 
        {
            /*
             *  don't ask - do
             */

            vp = vmManager.get( vmName, sourceTemplate);

            /*
             *  if this exists, and autoload is on, we need to check
             *  where this VM came from
             */

            if ( vp != null && getAutoload() ) 
            {    
                /*
                 *  see if this VM came from a library.  Need to pass sourceTemplate
                 *  in the event namespaces are set, as it could be masked by local
                 */
                
                String lib = vmManager.getLibraryName( vmName, sourceTemplate );

                if (lib != null)
                {
                    try 
                    {
                        /*
                         *  get the template from our map
                         */

                        Twonk tw = (Twonk) libModMap.get( lib );
                        
                        if ( tw != null)
                        {
                            Template template = tw.template;
                            
                            /*
                             *  now, compare the last modified time of the resource
                             *  with the last modified time of the template
                             *  if the file has changed, then reload. Otherwise, we should
                             *  be ok.
                             */

                            long tt = tw.modificationTime;
                            long ft = template.getResourceLoader().getLastModified( template );

                            if ( ft > tt )
                            {
                                debug("auto-reloading VMs from VM library : "+lib);
             
                                /*
                                 *  when there are VMs in a library that invoke each other,
                                 *  there are calls into getVelocimacro() from the init() 
                                 *  process of the VM directive.  To stop the infinite loop
                                 *  we save the current time reported by the resource loader
                                 *  and then be honest when the reload is complete
                                 */
                                 
                                tw.modificationTime = ft;
                                                                       
                                template = rsvc.getTemplate( lib );
 
                                /*
                                 * and now we be honest
                                 */

                                tw.template = template;
                                tw.modificationTime = template.getLastModified();

                                /*
                                 *  note that we don't need to put this twonk back 
                                 *  into the map, as we can just use the same reference
                                 *  and this block is synchronized
                                 */                                  
                             }
                         } 
                    }
                    catch (Exception e)
                    {
                        log.error("Velocimacro : Error using VM library : "+lib, e);
                    }

                    /*
                     *  and get again
                     */

                    vp = vmManager.get( vmName, sourceTemplate);
                }
            }
        }
        
        return vp;
    }

    /**
     *  tells the vmManager to dump the specified namespace
     */
    public boolean dumpVMNamespace( String namespace )
    {
        return vmManager.dumpNamespace( namespace );
    }

    /**
     *  sets permission to have VMs local in scope to their declaring template
     *  note that this is really taken care of in the VMManager class, but
     *  we need it here for gating purposes in addVM
     *  eventually, I will slide this all into the manager, maybe.
     */   
    private void setTemplateLocalInline( boolean b )
    {
        templateLocal = b;
    }

    private boolean getTemplateLocalInline()
    {
        return templateLocal;
    }

    /**
     *   sets the permission to add new macros
     */
    private boolean setAddMacroPermission( boolean arg )
    {
        boolean b = addNewAllowed;
        
        addNewAllowed = arg;
        return b;
    }

    /**
     *    sets the permission for allowing addMacro() calls to 
     *    replace existing VM's
     */
    private boolean setReplacementPermission( boolean arg )
    {
        boolean b = replaceAllowed;
        replaceAllowed = arg;
        return b;
    }

    /**
     *  set the switch for automatic reloading of
     *  global library-based VMs
     */
    private void setAutoload( boolean b)
    {
        autoReloadLibrary = b;
    }
    
    /**
     *  get the switch for automatic reloading of
     *  global library-based VMs
     */
    private boolean getAutoload()
    {
        return autoReloadLibrary;
    }

    /**
     * small continer class to hold the duple
     * of a template and modification time.
     * We keep the modification time so we can 
     * 'override' it on a reload to prevent
     * recursive reload due to inter-calling
     * VMs in a library
     */
    private static class Twonk
    {
        public Template template;
        public long modificationTime;
    }
}







