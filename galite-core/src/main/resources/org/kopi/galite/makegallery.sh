#!/bin/sh

printHeader() {
echo "<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">"
echo "<html>"
echo "  <head>"
echo "    <title>Kopi Icons</title>"
echo "  </head>"
echo ""
echo "  <body>"
echo "    <h1>Kopi Icons</h1>"
echo "    <table>"
}

printFooter() {
echo "    </table>"
echo "  </body>"
echo "</html>"
}

printLineStart() {
    echo "<tr>"
}

printLineEnd() {
    echo "</tr>"
}

printIcon() {
echo "	<td>"
echo "	  <table>"
echo "	    <tr>"
echo "	      <td>"
echo "		<img src=\"$1\">"
echo "	      </td>"
echo "	    </tr>"
echo "	    <tr>"
echo "	      <td>"
echo "		`basename $1 .gif`"
echo "	      </td>"
echo "	    </tr>"
echo "	  </table>"
echo "	</td>"
}

COUNT=0

printHeader

while [ $# -gt 0 ]; do
    if [ `expr $COUNT % 10` -eq 0 ]; then
        printLineStart
    fi

    FILENAME=$1
    shift

    printIcon $FILENAME

    COUNT=`expr $COUNT + 1`

    if [ `expr $COUNT % 10` -eq 0 ]; then
        printLineEnd
    fi
done

printFooter
