package org.kopi.galite.util

class FaxStatus {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR FOR OUTGOING FAXES
  // ----------------------------------------------------------------------
  constructor(id: String,
                tag: String,
                user: String,
                dialNo: String,
                state: String,
                pages: String,
                dials: String,
                text: String) {
    this.id = if (id.compareTo("") == 0) null else id
    this.tag = if (tag.compareTo("") == 0) null else tag
    this.user = if (user.compareTo("") == 0) null else user
    this.dialNo = if (dialNo.compareTo("") == 0) null else dialNo
    this.state = if (state.compareTo("") == 0) null else state
    this.pages = if (pages.compareTo("") == 0) null else pages
    this.text = if (text.compareTo("") == 0) null else text
    this.dials = if (dials.compareTo("") == 0) null else dials
    // not used
    /*
    if (isSent()) {		// TRY TO GATHER THE SEND TIME
      this.sendtime = Fax.readSendtime(id);
    }*/
  }

  // ----------------------------------------------------------------------
  // CONSTRUCTOR FOR INCOMING FAXES
  // ----------------------------------------------------------------------

  // ----------------------------------------------------------------------
  // CONSTRUCTOR FOR INCOMING FAXES
  // ----------------------------------------------------------------------
  fun FaxStatus(filename: String,
                incomingtime: String,
                sender: String,
                pages: String,
                duration: String,
                text: String) {
    this.filename = if (filename.compareTo("") == 0) null else filename
    this.incomingtime = if (incomingtime.compareTo("") == 0) null else incomingtime
    this.sender = if (sender.compareTo("") == 0) null else sender
    this.pages = if (pages.compareTo("") == 0) null else pages
    this.duration = if (duration.compareTo("") == 0) null else duration
    this.text = if (text.compareTo("") == 0) null else text
  }

  // ----------------------------------------------------------------------
  // RETURNS THE ID (DATABASE ID) INSIDE THE TAG
  // THE ID IS A NUMBER SO STRIP THEREFORE ANY OTHER LEADING CHARACTERS
  // IF NO ID IS FOUND RETURN -1
  // ----------------------------------------------------------------------

  // ----------------------------------------------------------------------
  // RETURNS THE ID (DATABASE ID) INSIDE THE TAG
  // THE ID IS A NUMBER SO STRIP THEREFORE ANY OTHER LEADING CHARACTERS
  // IF NO ID IS FOUND RETURN -1
  // ----------------------------------------------------------------------
  fun getTagId(): Int {
    var startpos = 0
    if (tag == null) {
      return -1
    }
    for (i in 0 until tag!!.length) {
      val b = tag!![i]
      if (b >= '0' && b <= '9') {
        startpos = i
        break
      }
    }
    var id = -1
    id = try {
      Integer.valueOf(tag!!.substring(startpos)).toInt()
    } catch (e: Exception) {
      return -1
    }
    return id
  }

  // ----------------------------------------------------------------------
  // RETURNS TRUE IF TAG STARTS WITH TAGSTR
  // ----------------------------------------------------------------------

  // ----------------------------------------------------------------------
  // RETURNS TRUE IF TAG STARTS WITH TAGSTR
  // ----------------------------------------------------------------------
  fun isTagged(tagstr: String?): Boolean {
    return if (tag == null) {
      false
    } else tag!!.startsWith(tagstr!!)
  }

  // ----------------------------------------------------------------------
  // RETURNS TRUE IF HAS BEEN SENT
  // ----------------------------------------------------------------------

  // ----------------------------------------------------------------------
  // RETURNS TRUE IF HAS BEEN SENT
  // ----------------------------------------------------------------------
  fun isSent(): Boolean {
    return if (state!!.compareTo("D") == 0) {
      text == null || text!!.compareTo("") == 0
    } else {
      false
    }
  }

  // ----------------------------------------------------------------------
  // SIMPLE ACCESSORS
  // ----------------------------------------------------------------------

  // ----------------------------------------------------------------------
  // SIMPLE ACCESSORS
  // ----------------------------------------------------------------------
  fun getId(): String? {
    return id
  }

  fun getTag(): String? {
    return tag
  }

  fun getUser(): String? {
    return user
  }

  fun getDialNo(): String? {
    return dialNo
  }

  fun getDials(): String? {
    return dials
  }

  fun getState(): String? {
    return state
  }

  fun getPages(): String? {
    return pages
  }

  fun getText(): String? {
    return text
  }

  fun getFilename(): String? {
    return filename
  }

  fun getIncomingtime(): String? {
    return incomingtime
  }

  fun getSender(): String? {
    return sender
  }

  fun getDuration(): String? {
    return duration
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS FOR OUTGOING FAXES
  // ----------------------------------------------------------------------

  // ----------------------------------------------------------------------
  // DATA MEMBERS FOR OUTGOING FAXES
  // ----------------------------------------------------------------------
  private var id: String? = null
  private var tag: String? = null
  private var user: String? = null
  private var dialNo: String? = null
  private var dials: String? = null
  private var state: String? = null
  private var filename: String? = null
  private var incomingtime: String? = null
  private var sender: String? = null
  private var duration: String? = null

  // ----------------------------------------------------------------------
  // DATA MEMBERS FOR BOTH IN/OUT FAXES
  // ----------------------------------------------------------------------

  // ----------------------------------------------------------------------
  // DATA MEMBERS FOR BOTH IN/OUT FAXES
  // ----------------------------------------------------------------------
  private var pages: String? = null
  private var text: String? = null
}
