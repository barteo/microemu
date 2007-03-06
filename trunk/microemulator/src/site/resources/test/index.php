<?php
    header('Content-Type: text');

    $random = (rand()%4);
    $messags = array("Hello!",
                     "Greetings",
                     "Welcome",
                     "Salute");

    print($messags[$random]);
    print(date(' H:m:s'));

?>
