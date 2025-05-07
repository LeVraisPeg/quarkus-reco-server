#include <tfhe/tfhe.h>    // Inclut les définitions de structures et de fonctions pour le chiffrement TFHE.
#include <tfhe/tfhe_io.h> // Inclut les fonctions d'entrée/sortie pour manipuler les clés TFHE.
#include <cstdlib>        // Fournit des fonctions standard pour la gestion des variables d'environnement.
#include <cstring>        // Fournit des fonctions pour manipuler des chaînes de caractères.
#include <cstdio>         // Fournit des fonctions pour la gestion des entrées/sorties en C, comme printf et fopen.
#include <fstream>        // Pour écrire dans un fichier
#include <sstream>        // Pour manipuler les flux de chaînes

extern "C"
{
    // Fonction pour générer des clés TFHE et les stocker dans des variables d'environnement.
    void generate_keys()
    {
        // Récupère les clés existantes depuis les variables d'environnement, si elles existent.
        const char *secret_env = std::getenv("TFHE_SECRET_KEY");
        const char *cloud_env = std::getenv("TFHE_CLOUD_KEY");

        // Si les clés existent déjà, affiche un message et termine la fonction.
        if (secret_env && cloud_env)
        {
            printf("Keys already exist in environment variables.\n");
            return;
        }

        // Définit le niveau de sécurité pour les paramètres TFHE.
        const int minimum_lambda = 110;

        // Crée un ensemble de paramètres de chiffrement basé sur le niveau de sécurité.
        TFheGateBootstrappingParameterSet *params = new_default_gate_bootstrapping_parameters(minimum_lambda);

        // Initialise une graine pour le générateur aléatoire.
        uint32_t seed[] = {123, 456, 789};
        tfhe_random_generator_setSeed(seed, 3); // Configure le générateur aléatoire avec la graine.

        // Génère un ensemble de clés (clé secrète et clé cloud) basé sur les paramètres.
        TFheGateBootstrappingSecretKeySet *key = new_random_gate_bootstrapping_secret_keyset(params);

        // Convertit la clé secrète en une chaîne de caractères et la stocke dans un flux mémoire.
        std::ostringstream secret_stream;
        std::ostringstream secret_stream;
        FILE *secret_temp = tmpfile();
        if (!secret_temp)
        {
            printf("Failed to create temporary file for secret key.\n");
            delete_gate_bootstrapping_secret_keyset(key);
            delete_gate_bootstrapping_parameters(params);
            return;
        }
        export_tfheGateBootstrappingSecretKeySet_toFile(secret_temp, key);
        rewind(secret_temp);
        char buffer[1024] = {0};
        fread(buffer, 1, sizeof(buffer), secret_temp);
        fclose(secret_temp);
        secret_stream << buffer;
        secret_env = strdup(secret_stream.str().c_str()); // Copie la chaîne générée.

        // Convertit la clé cloud en une chaîne de caractères et la stocke dans un flux mémoire.
        std::ostringstream cloud_stream;
        FILE *cloud_temp = tmpfile();
        if (!cloud_temp)
        {
            printf("Failed to create temporary file for cloud key.\n");
            delete_gate_bootstrapping_secret_keyset(key);
            delete_gate_bootstrapping_parameters(params);
            return;
        }
        export_tfheGateBootstrappingCloudKeySet_toFile(cloud_temp, &key->cloud);
        rewind(cloud_temp);
        char buffer[1024] = {0};
        fread(buffer, 1, sizeof(buffer), cloud_temp);
        fclose(cloud_temp);
        cloud_stream << buffer;
        cloud_env = strdup(cloud_stream.str().c_str()); // Copie la chaîne générée.

        // Définit les variables d'environnement avec les clés générées.
        _putenv_s("TFHE_SECRET_KEY", secret_env); // Stocke la clé secrète dans la variable d'environnement.
        _putenv_s("TFHE_CLOUD_KEY", cloud_env);   // Stocke la clé cloud dans la variable d'environnement.

        // Affiche un message indiquant que les clés ont été générées et stockées.
        printf("Keys generated and stored in environment variables.\n");

        // Libère la mémoire allouée pour les clés et les paramètres.
        delete_gate_bootstrapping_secret_keyset(key);
        delete_gate_bootstrapping_parameters(params);
    }

    // Fonction pour générer une clé TFHE et la stocker dans un fichier .env
    void generate_and_store_key()
    {
        // Vérifie si les clés existent déjà dans un fichier .env
        FILE *env_file = fopen(".env", "r");
        if (env_file)
        {
            printf("Keys already exist in .env file.\n");
            fclose(env_file);
            return;
        }

        // Définit le niveau de sécurité pour les paramètres TFHE.
        const int minimum_lambda = 110;

        // Crée un ensemble de paramètres de chiffrement basé sur le niveau de sécurité.
        TFheGateBootstrappingParameterSet *params = new_default_gate_bootstrapping_parameters(minimum_lambda);

        // Initialise une graine pour le générateur aléatoire.
        uint32_t seed[] = {123, 456, 789};
        tfhe_random_generator_setSeed(seed, 3);

        // Génère un ensemble de clés (clé secrète et clé cloud) basé sur les paramètres.
        TFheGateBootstrappingSecretKeySet *key = new_random_gate_bootstrapping_secret_keyset(params);

        // Utilise des fichiers temporaires pour stocker les clés.
        char secret_key_buffer[1024] = {0};
        char cloud_key_buffer[1024] = {0};

        // Écrit la clé secrète dans un fichier temporaire.
        FILE *secret_temp = tmpfile();
        if (!secret_temp)
        {
            printf("Failed to create temporary file for secret key.\n");
            delete_gate_bootstrapping_secret_keyset(key);
            delete_gate_bootstrapping_parameters(params);
            return;
        }
        export_tfheGateBootstrappingSecretKeySet_toFile(secret_temp, key);
        rewind(secret_temp); // Remet le pointeur au début du fichier temporaire.
        fread(secret_key_buffer, 1, sizeof(secret_key_buffer), secret_temp);
        fclose(secret_temp);

        // Écrit la clé cloud dans un fichier temporaire.
        FILE *cloud_temp = tmpfile();
        if (!cloud_temp)
        {
            printf("Failed to create temporary file for cloud key.\n");
            delete_gate_bootstrapping_secret_keyset(key);
            delete_gate_bootstrapping_parameters(params);
            return;
        }
        export_tfheGateBootstrappingCloudKeySet_toFile(cloud_temp, &key->cloud);
        rewind(cloud_temp); // Remet le pointeur au début du fichier temporaire.
        fread(cloud_key_buffer, 1, sizeof(cloud_key_buffer), cloud_temp);
        fclose(cloud_temp);

        // Écrit les clés dans un fichier .env
        std::ofstream env_out(".env");
        if (env_out.is_open())
        {
            env_out << "TFHE_SECRET_KEY=" << secret_key_buffer << "\n";
            env_out << "TFHE_CLOUD_KEY=" << cloud_key_buffer << "\n";
            env_out.close();
            printf("Keys generated and stored in .env file.\n");
        }
        else
        {
            printf("Failed to open .env file for writing.\n");
        }

        // Libère la mémoire allouée.
        delete_gate_bootstrapping_secret_keyset(key);
        delete_gate_bootstrapping_parameters(params);
    }
}