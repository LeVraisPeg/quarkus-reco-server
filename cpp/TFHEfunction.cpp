#include <tfhe/tfhe.h>    // Inclut les définitions et fonctions principales de la bibliothèque TFHE.
#include <tfhe/tfhe_io.h> // Inclut les fonctions d'entrée/sortie pour manipuler les clés TFHE.
#include <cstdlib>        // Fournit des fonctions standard pour la gestion des variables d'environnement.
#include <cstring>        // Fournit des fonctions pour manipuler des chaînes de caractères.
#include <cstdio>         // Fournit des fonctions pour la gestion des entrées/sorties en C, comme printf et fopen.

extern "C"
{
    void encrypt(const char *message, const char *secret_key_env, const char *cloud_key_env)
    {
        // Récupère la clé secrète depuis les variables d'environnement.
        const char *secret_key = std::getenv(secret_key_env);
        if (!secret_key)
        {
            printf("Secret key not found in environment variables.\n");
            return;
        }

        // Récupère la clé cloud depuis les variables d'environnement.
        const char *cloud_key = std::getenv(cloud_key_env);
        if (!cloud_key)
        {
            printf("Cloud key not found in environment variables.\n");
            return;
        }

        // Charge la clé secrète depuis le flux mémoire.
        FILE *secret_mem = fmemopen((void *)secret_key, strlen(secret_key), "r");
        TFheGateBootstrappingSecretKeySet *key = new_tfheGateBootstrappingSecretKeySet_fromFile(secret_mem);
        fclose(secret_mem);

        // Charge la clé cloud depuis le flux mémoire.
        FILE *cloud_mem = fmemopen((void *)cloud_key, strlen(cloud_key), "r");
        TFheGateBootstrappingCloudKeySet *cloud_key_set = new_tfheGateBootstrappingCloudKeySet_fromFile(cloud_mem);
        fclose(cloud_mem);

        // Crée un message chiffré à partir du message en clair.
        LweSample *ciphertext = new_gate_bootstrapping_ciphertext(key->params);
        bootsSymEncrypt(ciphertext, message[0], &key->lwe_key); // Chiffre le message avec la clé secrète.

        // Affiche le message chiffré.
        printf("Encrypted message: ");
        for (int i = 0; i < key->params->N; i++)
            printf("%d ", ciphertext->a[i]);
        printf("\n");

        // Libère la mémoire allouée pour le message chiffré et les clés.
        delete_gate_bootstrapping_ciphertext(ciphertext);
    }

    void decrypt(const char *ciphertext_env, const char *secret_key_env)
    {
        // Récupère la clé secrète depuis les variables d'environnement.
        const char *secret_key = std::getenv(secret_key_env);
        if (!secret_key)
        {
            printf("Secret key not found in environment variables.\n");
            return;
        }

        // Charge la clé secrète depuis le flux mémoire.
        FILE *secret_mem = fmemopen((void *)secret_key, strlen(secret_key), "r");
        TFheGateBootstrappingSecretKeySet *key = new_tfheGateBootstrappingSecretKeySet_fromFile(secret_mem);
        fclose(secret_mem);

        // Récupère le message chiffré depuis les variables d'environnement.
        const char *ciphertext_str = std::getenv(ciphertext_env);
        if (!ciphertext_str)
        {
            printf("Ciphertext not found in environment variables.\n");
            return;
        }

        // Crée un message chiffré à partir de la chaîne de caractères.
        LweSample *ciphertext = new_gate_bootstrapping_ciphertext(key->params);
        for (int i = 0; i < key->params->N; i++)
            ciphertext->a[i] = ciphertext_str[i] - '0'; // Convertit chaque caractère en entier.

        // Déchiffre le message chiffré avec la clé secrète.
        int decrypted_message = bootsSymDecrypt(ciphertext, &key->lwe_key); // Déchiffre le message.

        // Affiche le message déchiffré.
        printf("Decrypted message: %d\n", decrypted_message);

        // Libère la mémoire allouée pour le message chiffré et la clé.
        delete_gate_bootstrapping_ciphertext(ciphertext);
    }
}
