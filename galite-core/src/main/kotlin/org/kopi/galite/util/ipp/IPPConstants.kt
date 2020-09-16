package org.kopi.galite.util.ipp

object IPPConstants {
  const val TAG_ZERO = 0x00
  const val TAG_OPERATION = 0x01
  const val TAG_JOB = 0x02
  const val TAG_END = 0x03
  const val TAG_PRINTER = 0x04
  const val TAG_UNSUPPORTED_GROUP = 0x05
  const val TAG_SUBSCRIPTION = 0x06
  const val TAG_EVENT_NOTIFICATION = 0x07
  const val TAG_UNSUPPORTED_VALUE = 0x10
  const val TAG_DEFAULT = 0x11
  const val TAG_UNKNOWN = 0x12
  const val TAG_NOVALUE = 0x13
  const val TAG_NOTSETTABLE = 0x15
  const val TAG_DELETEATTR = 0x16
  const val TAG_ADMINDEFINE = 0x17
  const val TAG_INTEGER = 0x21
  const val TAG_BOOLEAN = 0x22
  const val TAG_ENUM = 0x23
  const val TAG_STRING = 0x30
  const val TAG_DATE = 0x31
  const val TAG_RESOLUTION = 0x32
  const val TAG_RANGE = 0x33
  const val TAG_BEGIN_COLLECTION = 0x34
  const val TAG_TEXTLANG = 0x35
  const val TAG_NAMELANG = 0x36
  const val TAG_END_COLLECTION = 0x37
  const val TAG_TEXT = 0x41
  const val TAG_NAME = 0x42
  const val TAG_KEYWORD = 0x44
  const val TAG_URI = 0x45
  const val TAG_URISCHEME = 0x46
  const val TAG_CHARSET = 0x47
  const val TAG_LANGUAGE = 0x48
  const val TAG_MIMETYPE = 0x49
  const val TAG_MEMBERNAME = 0x4A
  const val TAG_MASK = 0x7FFFFFFF
  const val TAG_COPY = -0x7fffffff
  const val OPS_PRINT_JOB: Short = 0x0002
  const val OPS_VALIDATE_JOB: Short = 0x0004
  const val OPS_CREATE_JOB: Short = 0x0005
  const val OPS_SEND_DOCUMENT: Short = 0x0006
  const val OPS_CANCEL_JOB: Short = 0x0008
  const val OPS_GET_JOB_ATTRIBUTES: Short = 0x0009
  const val OPS_GET_JOBS: Short = 0x000A
  const val OPS_GET_PRINTER_ATTRIBUTES: Short = 0x000B
  const val OPS_HOLD_JOB: Short = 0x000C
  const val OPS_RELEASE_JOB: Short = 0x000D
  const val OPS_PAUSE_PRINTER: Short = 0x0010
  const val OPS_RESUME_PRINTER: Short = 0x0011
  const val OPS_PURGE_JOBS: Short = 0x0012
  const val OPS_SET_JOB_ATTRIBUTES: Short = 0x0014


  val ERR_SUCCESSFUL = arrayOf(
          "successful-ok",  //0x0000
          "successful-ok-ignored-or-substituted-attributes ",  //0x0001
          "successful-ok-conflicting-attributes" //0x0002
  )
  val ERR_CLIENT_ERROR = arrayOf(
          "client-error-bad-request",  //0x0400
          "client-error-forbidden",  //0x0401
          "client-error-not-authenticated",  //0x0402
          "client-error-not-authorized",  //0x0403
          "client-error-not-possible",  //0x0404
          "client-error-timeout",  //0x0405
          "client-error-not-found",  //0x0406
          "client-error-gone",  //0x0407
          "client-error-request-entity-too-large",  //0x0408
          "client-error-request-value-too-long",  //0x0409
          "client-error-document-format-not-supported",  //0x040A
          "client-error-attributes-or-values-not-supported",  //0x040B
          "client-error-uri-scheme-not-supported",  //0x040C
          "client-error-charset-not-supported",  //0x040D
          "client-error-conflicting-attributes",  //0x040E
          "client-error-compression-not-supported",  //0x040F
          "client-error-compression-error",  //0x0410
          "client-error-document-format-error",  //0x0411
          "client-error-document-access-error" //0x0412
  )
  val ERR_SERVER_ERROR = arrayOf(
          "server-error-internal-error",  //0x0500
          "server-error-operation-not-supported",  //0x0501
          "server-error-service-unavailable",  //0x0502
          "server-error-version-not-supported",  //0x0503
          "server-error-device-error",  //0x0504
          "server-error-temporary-error",  //0x0505
          "server-error-not-accepting-jobs",  //0x0506
          "server-error-busy",  //0x0507
          "server-error-job-canceled",  //0x0508
          "server-error-multiple-document-jobs-not-supported" //0x0509
  )
}