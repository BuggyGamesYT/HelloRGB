set -e
export JAVA_HOME=/tmp/jdk PATH=/tmp/jdk/bin:$PATH QEMU_LD_PREFIX=/
BT=/tmp/bt/android-37.0; AJ=/tmp/plat/android-37.0/android.jar
export LD_LIBRARY_PATH=$BT/lib64
rm -rf build && mkdir -p build/obj build/dex build/gen
qemu-x86_64 $BT/aapt2 compile --dir res -o build/res.zip
qemu-x86_64 $BT/aapt2 link -o build/unsigned.apk -I $AJ --manifest AndroidManifest.xml --java build/gen build/res.zip
javac --release 17 -nowarn -cp $AJ -d build/obj $(find src build/gen -name '*.java')
java -cp $BT/lib/d8.jar com.android.tools.r8.D8 --min-api 24 --lib $AJ --output build/dex $(find build/obj -name '*.class')
(cd build/dex && zip -q ../unsigned.apk classes.dex)
qemu-x86_64 $BT/zipalign -f -p 4 build/unsigned.apk build/aligned.apk
[ -f ../hellorgb.keystore ] || keytool -genkeypair -keystore ../hellorgb.keystore -alias hellorgb -keyalg RSA -keysize 2048 -validity 10000 -storepass hellorgb -keypass hellorgb -dname "CN=Buggy Games YT" >/dev/null 2>&1
java -jar $BT/lib/apksigner.jar sign --ks ../hellorgb.keystore --ks-pass pass:hellorgb --out HelloRGB.apk build/aligned.apk
java -jar $BT/lib/apksigner.jar verify --verbose HelloRGB.apk | head -5
ls -la HelloRGB.apk
