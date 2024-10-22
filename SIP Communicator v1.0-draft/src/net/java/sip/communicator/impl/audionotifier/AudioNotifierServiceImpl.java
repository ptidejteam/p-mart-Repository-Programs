/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.audionotifier;

import java.net.*;
import java.util.*;

import net.java.sip.communicator.service.audionotifier.*;

/**
 * The implementation of the AudioNotifierService.
 *
 * @author Yana Stamcheva
 */
public class AudioNotifierServiceImpl
    implements AudioNotifierService
{
    private static final Map<String, SCAudioClipImpl> audioClips =
        new HashMap<String, SCAudioClipImpl>();

    private boolean isMute;

    /**
     * Creates an SCAudioClip from the given URI and adds it to the list of
     * available audio-s.
     *
     * @param uri the path where the audio file could be found
     */
    public SCAudioClip createAudio(String uri)
    {
        SCAudioClipImpl audioClip;

        synchronized (audioClips)
        {
            if(audioClips.containsKey(uri))
            {
                audioClip = audioClips.get(uri);
            }
            else
            {
                URL url =
                    AudioNotifierActivator.getResources().getSoundURLForPath(uri);

                if (url == null)
                {
                    // Not found by the class loader. Perhaps it's a local file.
                    try
                    {
                        url = new URL(uri);
                    }
                    catch (MalformedURLException e)
                    {
                        //logger.error("The given uri could not be parsed.", e);
                        return null;
                    }
                }
                
                try
                {
                    audioClip = new SCAudioClipImpl(url, this);
                }
                catch (Throwable e)
                {
                    // Cannot create audio to play
                    return null;
                }

                audioClips.put(uri, audioClip);
            }
        }

        return audioClip;
    }

    /**
     * Removes the given audio from the list of available audio clips.
     *
     * @param audioClip the audio to destroy
     */
    public void destroyAudio(SCAudioClip audioClip)
    {
        synchronized (audioClips) {
            audioClips.remove(audioClip);
        }
    }

    /**
     * Enables or disables the sound in the application. If FALSE, we try to
     * restore all looping sounds if any.
     *
     * @param isMute when TRUE disables the sound, otherwise enables the sound.
     */
    public void setMute(boolean isMute)
    {
        this.isMute = isMute;

        for (SCAudioClipImpl audioClip : audioClips.values())
        {
            if (isMute)
            {
                audioClip.internalStop();
            }
            else if (audioClip.isLooping())
            {
                audioClip.playInLoop(audioClip.getLoopInterval());
            }
        }
    }

    /**
     * Returns TRUE if the sound is currently disabled, FALSE otherwise.
     * @return TRUE if the sound is currently disabled, FALSE otherwise
     */
    public boolean isMute()
    {
        return isMute;
    }
}
