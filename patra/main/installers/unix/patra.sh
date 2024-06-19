#!/bin/bash

APPJAR="patra-1.4.jar"

# Change the directory in case we were started from a graphical shell like Nautilus
cd "`dirname "$0"`"

# Messages
MSG0="Loading program:"
MSG1="Starting program..."
MSG2="Java exec found in "
MSG3="Your java version is too old "
MSG4="You need to upgrade to JRE 1.6.x or newer from http://www.java.com"
MSG5="Suitable java version found "
MSG6="Configuring environment..."
MSG7="You don't seem to have a valid JRE. This program works best with Sun JRE available at http://www.java.com "
MSG8="Unable to locate java exec in "
MSG9=" hierarchy"
MSG10="Java exec not found in PATH, starting auto-search..."
MSG11="Java exec found in PATH. Verifying..."

look_for_java()
{
  JAVADIR=/usr/lib/jvm
  if look_for_javaImpl ; then
    return 0
  fi
  JAVADIR=/usr/lib
  if look_for_javaImpl ; then
    return 0
  fi
  JAVADIR=/usr/java
  if look_for_javaImpl ; then
    return 0
  fi
  JAVADIR=/opt
  if look_for_javaImpl ; then
    return 0
  fi
  return 1
}

look_for_javaImpl()
{
  IFS=$'\n'
  potential_java_dirs=(`ls -d1 "$JAVADIR"/j* | sort | tac`)
  for D in "${potential_java_dirs[@]}"; do
    if [[ -d "$D" && -x "$D/bin/java" ]]; then
      JAVA_PROGRAM_DIR="$D/bin/"
      echo $MSG2 $JAVA_PROGRAM_DIR
      if check_version ; then
    return 0
      fi
    fi
  done
  echo $MSG8 "${JAVADIR}/" $MSG9 ; echo $MSG4
  return 1
}

check_version()
{
  # Short-circuit GCJ
  ISGCJ=`${JAVA_PROGRAM_DIR}java -version 2>&1 | grep -i gcj`
  if [ "$ISGCJ" != "" ] ; then
    echo $MSG7
    return 1
  fi

  JAVA_HEADER=`${JAVA_PROGRAM_DIR}java -version 2>&1 | head -n 1`
  JAVA_IMPL=`echo ${JAVA_HEADER} | cut -f1 -d' '`
  if [ "$JAVA_IMPL" = "java" ] ; then
    VERSION=`echo ${JAVA_HEADER} | sed "s/java version \"\(.*\)\"/\1/"`
    if echo $VERSION | grep "^1.[0-5]" ; then
      echo $MSG3 "[${JAVA_PROGRAM_DIR}java = ${VERSION}]" ; echo $MSG4
      return 1
    else
      echo $MSG5 "[${JAVA_PROGRAM_DIR}java = ${VERSION}]" ; echo $MSG6
      return 0
    fi
  else
    echo $MSG7 "[${JAVA_PROGRAM_DIR}java = ${JAVA_IMPL}]" ; echo $MSG4
    return 1
  fi
}

echo $MSG1

# Locate and test the Java executable
if [ `uname` = "Linux" ]; then
  if ! command -v java &>/dev/null; then
    echo $MSG10
    if ! look_for_java ; then
      exit 1
    fi
  else
    echo $MSG11
    if ! check_version ; then
      if ! look_for_java ; then
        exit 1
      fi
    fi
  fi
else
  JAVA_PROGRAM_DIR=""
fi

echo $MSG0

export J2SE_PREEMPTCLOSE=1

# If the script that called us gave us an executable, pass it along
if echo "$1" | grep -q "\-Dunix.executable="
then
  EXECUTABLE=$1
  ARGUMENTS="$2 $3 $4 $5"
else
  # No executable given, just pass whatever arguments there were along
  EXECUTABLE=""
  ARGUMENTS=$*
fi

${JAVA_PROGRAM_DIR}java -ea -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.NoOpLog -Djava.library.path=. $EXECUTABLE -jar $APPJAR $ARGUMENTS
if [ $? -ne 0 ]; then
  echo
  echo "Something went wrong with starting the Java program."
  echo "Maybe you're using the wrong version of Java?"
  echo "This program is tested against and works best with with Sun's JRE, Java 1.6+."
  echo "The version of Java in your PATH is:"
  java -version
  echo
fi