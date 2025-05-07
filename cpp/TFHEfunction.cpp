#include <tfhe/tfhe.h>
#include <tfhe/tfhe_io.h>
#include <cstdlib>
#include <cstring>
#include <cstdio>

extern "C"
{
    // Fonction de chiffrement
    void encrypt(const char *message, const char *secret_key_env, const char *cloud_key_env)
    {
        // Récupère la clé secrète depuis les variables d'environnement
        const char *secret_key = std::getenv(secret_key_env);
        if (!secret_key)
        {
            printf("Secret key not found in environment variables.\n");
            return;
        }

        // Récupère la clé cloud depuis les variables d'environnement
        const char *cloud_key = std::getenv(cloud_key_env);
        if (!cloud_key)
        {
            printf("Cloud key not found in environment variables.\n");
            return;
        }

        // Charge la clé secrète depuis un fichier temporaire
        FILE *secret_mem = tmpfile();
        if (!secret_mem)
        {
            printf("Failed to create temporary file for secret key.\n");
            return;
        }
        fwrite(secret_key, 1, strlen(secret_key), secret_mem);
        rewind(secret_mem);
        TFheGateBootstrappingSecretKeySet *key = new_tfheGateBootstrappingSecretKeySet_fromFile(secret_mem);
        fclose(secret_mem);

        // Charge la clé cloud depuis un fichier temporaire
        FILE *cloud_mem = tmpfile();
        if (!cloud_mem)
        {
            printf("Failed to create temporary file for cloud key.\n");
            delete_gate_bootstrapping_secret_keyset(key);
            return;
        }
        fwrite(cloud_key, 1, strlen(cloud_key), cloud_mem);
        rewind(cloud_mem);
        TFheGateBootstrappingCloudKeySet *cloud_key_set = new_tfheGateBootstrappingCloudKeySet_fromFile(cloud_mem);
        fclose(cloud_mem);

        // Crée un message chiffré
        LweSample *ciphertext = new_gate_bootstrapping_ciphertext(key->params);
        bootsSymEncrypt(ciphertext, message[0], key);

        // Affiche le message chiffré
        printf("Encrypted message: ");
        for (int i = 0; i < key->params->in_out_params->n; i++)
        {
            printf("%d ", ciphertext->a[i]);
        }
        printf("\n");

        // Libère la mémoire
        delete_gate_bootstrapping_ciphertext(ciphertext);
        delete_gate_bootstrapping_secret_keyset(key);
        delete_gate_bootstrapping_cloud_keyset(cloud_key_set);
    }

    // Fonction de déchiffrement
    void decrypt(const char *ciphertext_env, const char *secret_key_env)
    {
        // Récupère la clé secrète depuis les variables d'environnement
        const char *secret_key = std::getenv(secret_key_env);
        if (!secret_key)
        {
            printf("Secret key not found in environment variables.\n");
            return;
        }

        // Charge la clé secrète depuis un fichier temporaire
        FILE *secret_mem = tmpfile();
        if (!secret_mem)
        {
            printf("Failed to create temporary file for secret key.\n");
            return;
        }
        fwrite(secret_key, 1, strlen(secret_key), secret_mem);
        rewind(secret_mem);
        TFheGateBootstrappingSecretKeySet *key = new_tfheGateBootstrappingSecretKeySet_fromFile(secret_mem);
        fclose(secret_mem);

        // Récupère le message chiffré depuis les variables d'environnement
        const char *ciphertext_str = std::getenv(ciphertext_env);
        if (!ciphertext_str)
        {
            printf("Ciphertext not found in environment variables.\n");
            delete_gate_bootstrapping_secret_keyset(key);
            return;
        }

        // Crée un message chiffré à partir de la chaîne de caractères
        LweSample *ciphertext = new_gate_bootstrapping_ciphertext(key->params);
        for (int i = 0; i < key->params->in_out_params->n; i++)
        {
            ciphertext->a[i] = ciphertext_str[i] - '0';
        }

        // Déchiffre le message
        int decrypted_message = bootsSymDecrypt(ciphertext, key);

        // Affiche le message déchiffré
        printf("Decrypted message: %d\n", decrypted_message);

        // Libère la mémoire
        delete_gate_bootstrapping_ciphertext(ciphertext);
        delete_gate_bootstrapping_secret_keyset(key);
    }
}
