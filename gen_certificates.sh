rm -f *.jks

keytool -genkey -alias users -keyalg RSA -validity 365 -keystore ./users.jks -storetype pkcs12 << EOF
123
123
Users.Users
TP2
SD2021
LX
LX
PT
yes
123
123
EOF

echo
echo
echo "Exporting Certificates"
echo
echo

keytool -exportcert -alias users -keystore users.jks -file users.cert << EOF
123
EOF

keytool -genkey -alias dirs -keyalg RSA -validity 365 -keystore ./dirs.jks -storetype pkcs12 << EOF
123
123
Directory.Directory
TP2
SD2021
LX
LX
PT
yes
123
123
EOF

echo
echo
echo "Exporting Certificates"
echo
echo

keytool -exportcert -alias dirs -keystore dirs.jks -file dirs.cert << EOF
123
EOF

keytool -genkey -alias files -keyalg RSA -validity 365 -keystore ./files.jks -storetype pkcs12 << EOF
123
123
Files.Files
TP2
SD2021
LX
LX
PT
yes
123
123
EOF

echo
echo
echo "Exporting Certificates"
echo
echo

keytool -exportcert -alias users -keystore users.jks -file users.cert << EOF
123users
EOF

echo "Creating Client Truststore"
cp cacerts client-ts.jks
keytool -importcert -file users.cert -alias users -keystore client-ts.jks << EOF
changeit
yes
EOF

keytool -importcert -file dirs.cert -alias dirs -keystore client-ts.jks << EOF
changeit
yes
EOF

keytool -importcert -file files.cert -alias files -keystore client-ts.jks << EOF
changeit
yes
EOF

