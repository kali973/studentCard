/*
 * Copyright ? 1998 Gemplus SCA
 * Av. du Pic de Bertagne - Parc d'Activit?s de G?menos
 * BP 100 - 13881 G?menos CEDEX
 * 
 * "Code derived from the original OpenCard Framework".
 * 
 * Everyone is allowed to redistribute and use this source  (source
 * code)  and binary (object code),  with or  without modification,
 * under some conditions:
 * 
 *  - Everyone  must  retain  and/or  reproduce the above copyright
 *    notice,  and the below  disclaimer of warranty and limitation
 *    of liability  for redistribution and use of these source code
 *    and object code.
 * 
 *  - Everyone  must  ask a  specific prior written permission from
 *    Gemplus to use the name of Gemplus.
 * 
 *  - In addition,  modification and redistribution of this  source
 *    code must retain the below original copyright notice.
 * 
 * DISCLAIMER OF WARRANTY
 * 
 * THIS CODE IS PROVIDED "AS IS",  WITHOUT ANY WARRANTY OF ANY KIND
 * (INCLUDING,  BUT  NOT  LIMITED  TO,  THE IMPLIED  WARRANTIES  OF
 * MERCHANTABILITY  AND FITNESS FOR  A  PARTICULAR PURPOSE)  EITHER
 * EXPRESS OR IMPLIED.  GEMPLUS DOES NOT WARRANT THAT THE FUNCTIONS
 * CONTAINED  IN THIS SOFTWARE WILL MEET THE USER'S REQUIREMENTS OR
 * THAT THE OPERATION OF IT WILL BE UNINTERRUPTED OR ERROR-FREE. NO
 * USE  OF  ANY  CODE  IS  AUTHORIZED  HEREUNDER EXCEPT UNDER  THIS
 * DISCLAIMER.
 * 
 * LIMITATION OF LIABILITY
 * 
 * GEMPLUS SHALL NOT BE LIABLE FOR INFRINGEMENTS OF  THIRD  PARTIES
 * RIGHTS. IN NO EVENTS, UNLESS REQUIRED BY APPLICABLE  LAW,  SHALL
 * GEMPLUS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES OF ANY CHARACTER  INCLUDING,
 * WITHOUT LIMITATION, DAMAGES FOR LOSS OF GOODWILL, WORK STOPPAGE,
 * COMPUTER FAILURE OR MALFUNCTION, OR ANY AND ALL OTHER DAMAGES OR
 * LOSSES, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. ALSO,
 * GEMPLUS IS  UNDER NO  OBLIGATION TO MAINTAIN,  CORRECT,  UPDATE, 
 * CHANGE, MODIFY, OR OTHERWISE SUPPORT THIS SOFTWARE.
 */

/*
 * Copyright ? 1997, 1998 IBM Corporation.
 * 
 * Redistribution and use in source (source code) and binary (object code)
 * forms, with or without modification, are permitted provided that the
 * following conditions are met:
 * 1. Redistributed source code must retain the above copyright notice, this
 * list of conditions and the disclaimer below.
 * 2. Redistributed object code must reproduce the above copyright notice,
 * this list of conditions and the disclaimer below in the documentation
 * and/or other materials provided with the distribution.
 * 3. The name of IBM may not be used to endorse or promote products derived
 * from this software or in any other form without specific prior written
 * permission from IBM.
 * 4. Redistribution of any modified code must be labeled "Code derived from
 * the original OpenCard Framework".
 *
 * THIS SOFTWARE IS PROVIDED BY IBM "AS IS" FREE OF CHARGE. IBM SHALL NOT BE
 * LIABLE FOR INFRINGEMENTS OF THIRD PARTIES RIGHTS BASED ON THIS SOFTWARE.  ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IBM DOES NOT WARRANT THAT THE FUNCTIONS CONTAINED IN THIS
 * SOFTWARE WILL MEET THE USER'S REQUIREMENTS OR THAT THE OPERATION OF IT WILL
 * BE UNINTERRUPTED OR ERROR-FREE.  IN NO EVENT, UNLESS REQUIRED BY APPLICABLE
 * LAW, SHALL IBM BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.  ALSO, IBM IS UNDER NO OBLIGATION
 * TO MAINTAIN, CORRECT, UPDATE, CHANGE, MODIFY, OR OTHERWISE SUPPORT THIS
 * SOFTWARE.
 */

package lib.OCF1;

import java.io.*;
import java.lang.*;
import java.util.Properties;
import javax.comm.*;

import opencard.core.terminal.*;
import opencard.core.util.*;

import opencard.opt.terminal.protocol.*;

/**
 * PureJava-Implementation of an OpenCard <code>CardTerminal</code> for
 * Gemplus OROS-based reader (GCR400, GCR500) using the javax.comm package.
 *
 * IMPORTANT: with jdk1.1 javax.comm works only with signed applets  !!!
 *
 * @version $Id: OrosCardTerminal.java,v 3.0 1999/02/11 17:21:16 root Exp root $<br>
 * @author Stephan Breideneich (sbreiden@de.ibm.com)<br>
 * Patched by dave.durbin@jcp.co.uk (Mon, 30 Nov 1998) for GemCore compatibility<br>
 * Patched by Patrick.Biget@gemplus.com (Thu, 3 Dec 1998) -> com.gemplus zone<br>
 * Patched by Gilles.Pauzie@gemplus.com (Mon, 11 Jan 1999) New features, see<br>
 *  README file<br>
 * Patched by Gilles.Pauzie@gemplus.com (Fri, 22 Jan 1999) Renamed
 *   OrosCardTerminal. Extends from GemplusSerialCardTerminal class<br>
 * Patched by Gilles.Pauzie@gemplus.com (Tue, 9 Feb 1999) Bug fixed for
 *   cards only using T=1 protocol<br>
 *
 * @see opencard.core.terminal.CardTerminal
 * @see opencard.core.terminal.CardTerminalRegistry
 * @see opencard.core.terminal.CardTerminalFactory
 * @see opencard.core.terminal.Slot
 * @see opencard.core.terminal.CardID
 */

public class OrosCardTerminal extends GemplusSerialCardTerminal {

  /** command reader for an Iso In (T=0) command */
  private final byte ISO_IN_T0_COMMAND = (byte) 0x14;

  /** command reader for an Iso Out (T=0) command */
  private final byte ISO_OUT_T0_COMMAND = (byte) 0x13;

  /** command reader for an APDU (T=1) command */
  private final byte APDU_T1_COMMAND = (byte) 0x15;

  /** command reader for a power down command */
  private final byte POWER_DOWN_COMMAND = (byte) 0x11;

  /** command reader for a power up and reset command */
  private final byte POWER_UP_AND_RESET_COMMAND = (byte) 0x12;

  /**
   * Instantiates a <tt>GemplusCardTerminal</tt> object with one slot.
   *
   * @param     name
   *            The user friendly name.
   * @param     type
   *            The terminal type (in this case GCR410 or GCR700).
   * @param     serialDevice
   *            The device name used for reader communication (e.g. COM1).
   * @exception CardTerminalException
   *            Thrown when the instantiation fails for any reason.
   */
  protected OrosCardTerminal(String name, String type, String serialDevice)
    throws CardTerminalException {

    super(name, type, serialDevice);

    // only one Icc interface for OROS-based readers
    addSlots(1);

  }


  /**
   * open serial port and init transfer protocol
   *
   * @exception CardTerminalException
   *            Thrown in case of errors during init process.
   */
  public void open() throws CardTerminalException {

    super.open("OROS-R2.2");
  } // open

  /** gets the firmware version from the reader
   */
  protected String getFirmwareVersion() throws CardTerminalException {
    byte[] FirmwareRequest = new byte[] { (byte)0x22, (byte)0x05,
                                          (byte)0x3F, (byte)0xF0, (byte)0x10 };
    byte[] response = null;
    byte[] version = null;

    try {

      response = protocolTransmit(FirmwareRequest);

      if (response == null) {
        tracerError("getFirmwareVersion", "no response from reader");

        throw new CardTerminalException("couldn't get firmware "
                                        + "version from reader");
      }

      if (response[0] != 0)
        throw new CardTerminalException("got errorcode " + response[0]
                      + " from reader on firmware version request");

      version = new byte[response.length - 1];
      System.arraycopy(response, 1, version, 0, response.length - 1);

      return new String(version);

    // remap Exception to CardTerminalException
    } catch (Exception e) {
      throw new CardTerminalException(e.toString());
    }

  }

  /**
   * updates the card status from a slot.
   *
   * @param     slotID
   *            The slot number of the slot requesting the status.
   * @exception CardTerminalException
   *            Thrown when error occurred getting status from reader.
   */
  protected void UpdateCardStatus(int slotID) throws CardTerminalException {
    byte[] sendBuf  = new byte[] {(byte)0x24,(byte)0x03};
    byte[] response = null;

    if (!getReaderClosed()) {
      try {
        tracerDebug("getCardStatus", "check reader-state");
        response = protocolTransmit(sendBuf);
      // remap Exception to CardTerminalException
      } catch (Exception e) {
        throw new CardTerminalException(e.toString());
      }
    }
    tracerDebug("UpdateCardStatus", "  Status: " + response.toString());
    // cache status for specified slot
    setCachedCardStatus(slotID,(response[1] & (byte) 0x04));

  }
  /**
   * power up card (with reset)
   *
   * @param     slotID
   *            slot number with the inserted card
   * @exception CardTerminalException
   *            Thrown when power up method failed.
   */
  public byte[] powerUpCard(int slotID) throws CardTerminalException {

    byte[] powerUpRequest = new byte[] {POWER_UP_AND_RESET_COMMAND};

    return super.powerUpCard(slotID,powerUpRequest);
  } // powerUpCard

  /**
   * power down card
   *
   * @param     slotID
   *            slot number with the inserted card
   * @exception CardTerminalException
   *            Thrown when power down method failed.
   */
  protected void powerDownCard(int slotID)throws CardTerminalException {

    byte[] powerDownRequest = new byte[] {POWER_DOWN_COMMAND};
    super.powerDownCard(slotID,powerDownRequest);
  }

  /** The <tt>internalSendAPDU</tt>
   *
   * @param     slot
   *            The slot number of the slot used.
   * @param     capdu
   *            The <tt>CommandAPDU</tt> to send.
   * @param     ms
   *            A timeout in milliseconds.
   * @return    A <tt>ResponseAPDU</tt>.
   */
  public ResponseAPDU internalSendAPDU(int slotID,CommandAPDU capdu,int ms)
          throws CardTerminalException {

    return super.internalSendAPDU(slotID,capdu,ms,
                                  ISO_IN_T0_COMMAND,
                                  ISO_OUT_T0_COMMAND,
                                  APDU_T1_COMMAND);
  }


}
