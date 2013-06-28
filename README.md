correctionIut
=============

# Correction des TPs de la formation IUT

Pour t�l�charger le TP localement:

    git clone https://github.com/Ninja-Squad/correctionIut.git

Chaque �tape du TP est impl�ment�e dans une branche:

 - etape1_maven_collections
 - etape2_test_unitaires_collections
 - etape3_joda_time
 - etape4_guava
 - etape6_lambda
 - etape7_dao_tests_persistance
 - etape8_annotations
 - etape9_selenium
     
Chaque �tape part de l'�tape pr�c�dente. Pour observer la correction de l'�tape 1:

     git checkout etape1_maven_collections
     
Pour observer le code de l'�tape 9 :

    git checkout etape9_selenium
    
Les �tapes 1 � 4 sont impl�ment�es avec Java 7.
Les �tapes 6 � 8 sont impl�ment�es avec Java 8 (bien que seules les �volutions apport�es par l'�tape 6 n�cessitent Java 8)
