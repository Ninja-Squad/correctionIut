correctionIut
=============

# Correction des TPs de la formation IUT

Pour télécharger le TP localement:

    git clone https://github.com/Ninja-Squad/correctionIut.git

Chaque étape du TP est implémentée dans une branche:

 - etape1_maven_collections
 - etape2_test_unitaires_collections
 - etape3_joda_time
 - etape4_guava
 - etape6_lambda
 - etape7_dao_tests_persistance
 - etape8_annotations
 - etape9_selenium
     
Chaque étape part de l'étape précédente. Pour observer la correction de l'étape 1:

     git checkout etape1_maven_collections
     
Pour observer le code de l'étape 9 :

    git checkout etape9_selenium
    
Les étapes 1 à 4 sont implémentées avec Java 7.
Les étapes 6 à 8 sont implémentées avec Java 8 (bien que seules les évolutions apportées par l'étape 6 nécessitent Java 8)
