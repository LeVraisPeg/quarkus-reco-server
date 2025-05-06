#include <tfhe/tfhe.h>    // Inclut les définitions et fonctions principales de la bibliothèque TFHE.
#include <tfhe/tfhe_io.h> // Inclut les fonctions d'entrée/sortie pour manipuler les clés TFHE.
#include <cstdlib>        // Fournit des fonctions standard pour la gestion des variables d'environnement.
#include <cstring>        // Fournit des fonctions pour manipuler des chaînes de caractères.
#include <cstdio>         // Fournit des fonctions pour la gestion des entrées/sorties en C, comme printf et fopen.

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
        FILE *secret_mem = open_memstream(&secret_env, nullptr);
        export_tfheGateBootstrappingSecretKeySet_toFile(secret_mem, key); // Exporte la clé secrète dans le flux.
        fclose(secret_mem);                                               // Ferme le flux mémoire.

        // Convertit la clé cloud en une chaîne de caractères et la stocke dans un flux mémoire.
        FILE *cloud_mem = open_memstream(&cloud_env, nullptr);
        export_tfheGateBootstrappingCloudKeySet_toFile(cloud_mem, &key->cloud); // Exporte la clé cloud dans le flux.
        fclose(cloud_mem);                                                      // Ferme le flux mémoire.

        // Définit les variables d'environnement avec les clés générées.
        setenv("TFHE_SECRET_KEY", secret_env, 1); // Stocke la clé secrète dans la variable d'environnement.
        setenv("TFHE_CLOUD_KEY", cloud_env, 1);   // Stocke la clé cloud dans la variable d'environnement.

        // Affiche un message indiquant que les clés ont été générées et stockées.
        printf("Keys generated and stored in environment variables.\n");

        // Libère la mémoire allouée pour les clés et les paramètres.
        delete_gate_bootstrapping_secret_keyset(key);
        delete_gate_bootstrapping_parameters(params);
    }
}