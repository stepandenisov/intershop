#!/bin/bash

INTERSHOP_JAR=".\intershop-core\target\intershop-core-0.0.1-SNAPSHOT.jar"
PAYMENT_JAR=".\payment\target\payment-0.0.1-SNAPSHOT.jar"

chmod +x "$INTERSHOP_JAR"
chmod +x "$PAYMENT_JAR"

"$PAYMENT_JAR" &
"$INTERSHOP_JAR"
