echo "Running LoggerExample:"

for i in ../../bin/*.jar
do
    _VELCP=$VELCP:"$i"
done

java -cp $_VELCP:. LoggerExample 

