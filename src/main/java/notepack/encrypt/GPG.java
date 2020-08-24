package notepack.encrypt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import notepack.app.domain.exception.MessageError;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPDataValidationException;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.bc.BcPGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyKeyEncryptionMethodGenerator;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;
import org.bouncycastle.util.io.Streams;

/*
  Lot of code used from repo https://github.com/subutai-io/peer-os/
 */
public class GPG {

    private String publicKeyPath;
    private String privateKeyPath;
    private String password;

    public GPG(String publicKeyFilename, String privateKeyFilename, String password) {
        this.publicKeyPath = publicKeyFilename;
        this.privateKeyPath = privateKeyFilename;
        this.password = password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] encrypt(byte[] content) {

        byte[] result = content;

        try {
            PGPPublicKey key = getPublicKeyFromFile(new FileInputStream(publicKeyPath));

            result = encrypt(content, key);

//            result = new String(res);

        } catch (IOException | PGPException ex) {
            Logger.getLogger(GPG.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public byte[] decrypt(byte[] content) throws MessageError {

        PGPPrivateKey privateKey = getPrivateKey();

        byte[] result;
        try {
            result = decrypt(content, privateKey);
        } catch (UnsupportedEncodingException ex) {
            throw new MessageError(ex.getMessage(), ex);
        } catch (PGPException | IOException ex) {
            throw new MessageError(ex.getMessage(), ex);
        }

        return result;
    }

    public boolean isPrivateKeyExists() {
        File f = new File(privateKeyPath);
        return f.exists();
    }

    public boolean isPublicKeyExists() {
        File f = new File(publicKeyPath);
        return f.exists();
    }

    public boolean isPrivateKeyLoaded() {
        try {
            PGPPrivateKey k = getPrivateKey();
        } catch (MessageError ex) {
            return false;
        }
        return true;
    }

    private PGPPrivateKey getPrivateKey() throws MessageError {
        PGPPrivateKey privateKey;
        try {
            PGPSecretKey secretKey = readSecretKey(new FileInputStream(privateKeyPath));

            privateKey = secretKey.extractPrivateKey(
                    new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(password.toCharArray()));

            long keyid = privateKey.getKeyID();

        } catch (FileNotFoundException ex) {
            throw new MessageError("Private key path not found: " + privateKeyPath, ex);
        } catch (IOException | PGPException ex) {
            throw new MessageError("Could not open private key, is password correct?", ex);
        }

        return privateKey;
    }

    private byte[] decrypt(byte encData[], PGPPrivateKey privateKey) throws PGPException, IOException {

        PGPPublicKeyEncryptedData pgpEncData = getPGPEncryptedData(encData);

        InputStream is = getInputStream(privateKey, pgpEncData);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Streams.pipeAll(is, bos);
        bos.close();
        byte data[] = bos.toByteArray();

        if (!pgpEncData.verify()) {
            throw new PGPDataValidationException("Data integrity check failed");
        }

        return data;
    }

    private InputStream getInputStream(PGPPrivateKey privateKey, PGPPublicKeyEncryptedData pgpEncData)
            throws PGPException, IOException {
        InputStream is = pgpEncData
                .getDataStream(new JcePublicKeyDataDecryptorFactoryBuilder().setProvider("BC").build(privateKey));

        JcaPGPObjectFactory objectFactory = new JcaPGPObjectFactory(is);

        Object message = objectFactory.nextObject();

        PGPCompressedData compressedData = (PGPCompressedData) message;

        JcaPGPObjectFactory pgpObjectFactory = new JcaPGPObjectFactory(compressedData.getDataStream());

        PGPLiteralData literalData = (PGPLiteralData) pgpObjectFactory.nextObject();

        return literalData.getInputStream();
    }

    private PGPPublicKeyEncryptedData getPGPEncryptedData(byte data[]) throws IOException {
        InputStream in = PGPUtil.getDecoderStream(new ByteArrayInputStream(data));

        JcaPGPObjectFactory objectFactory = new JcaPGPObjectFactory(in);

        PGPEncryptedDataList encryptedDataList = (PGPEncryptedDataList) objectFactory.nextObject();

        Iterator it = encryptedDataList.getEncryptedDataObjects();

        return (PGPPublicKeyEncryptedData) it.next();
    }

    private PGPSecretKey readSecretKey(InputStream is) throws IOException, PGPException {
        PGPSecretKeyRingCollection pgpSec
                = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(is), new BcKeyFingerprintCalculator());
        Iterator keyRingIter = pgpSec.getKeyRings();

        while (keyRingIter.hasNext()) {
            PGPSecretKeyRing keyRing = (PGPSecretKeyRing) keyRingIter.next();
            Iterator keyIter = keyRing.getSecretKeys();

            while (keyIter.hasNext()) {
                PGPSecretKey key = (PGPSecretKey) keyIter.next();

                if (key.isSigningKey()) {
                    return key;
                }
            }
        }

        throw new IllegalArgumentException("Can't find signing key in key ring.");
    }

    @SuppressWarnings("rawtypes")
    private PGPPublicKey getPublicKeyFromFile(InputStream in) throws IOException, PGPException {
        in = PGPUtil.getDecoderStream(in);
        PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(in, new BcKeyFingerprintCalculator());
        PGPPublicKey key = null;
        Iterator rIt = pgpPub.getKeyRings();
        while (key == null && rIt.hasNext()) {
            PGPPublicKeyRing kRing = (PGPPublicKeyRing) rIt.next();
            Iterator kIt = kRing.getPublicKeys();
            while (key == null && kIt.hasNext()) {
                PGPPublicKey k = (PGPPublicKey) kIt.next();
                if (k.isEncryptionKey()) {
                    key = k;
                }
            }
        }
        if (key == null) {
            throw new IllegalArgumentException("Can't find encryption key in key ring.");
        }
        return key;
    }

    private byte[] compress(byte data[]) throws IOException {
        PGPCompressedDataGenerator compressGen = new PGPCompressedDataGenerator(CompressionAlgorithmTags.ZIP);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        OutputStream compressOut = compressGen.open(bos);

        OutputStream os = new PGPLiteralDataGenerator().open(compressOut, PGPLiteralData.BINARY, "", data.length, new Date());

        os.write(data);
        os.close();

        compressGen.close();

        return bos.toByteArray();
    }

    private byte[] encrypt(byte data[], PGPPublicKey publicKey) throws IOException, PGPException {
        byte[] compressedData = compress(data);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        ArmoredOutputStream aos = new ArmoredOutputStream(bos);

        BcPGPDataEncryptorBuilder dataEncryptor = new BcPGPDataEncryptorBuilder(SymmetricKeyAlgorithmTags.AES_256);
        dataEncryptor.setWithIntegrityPacket(true);
        dataEncryptor.setSecureRandom(new SecureRandom());

        PGPEncryptedDataGenerator encryptedDataGenerator = new PGPEncryptedDataGenerator(dataEncryptor);
        encryptedDataGenerator.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(publicKey));

        OutputStream encOut = encryptedDataGenerator.open(aos, compressedData.length);

        encOut.write(compressedData);

        encOut.close();

        aos.close();

        return bos.toByteArray();
    }

}
