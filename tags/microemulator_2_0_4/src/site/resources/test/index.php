<?php
    header('Content-Type: text/plain');
    header("Cache-Control: no-cache, must-revalidate"); // HTTP/1.1
    header("Expires: Mon, 5 Mar 2007 05:00:00 GMT"); // Date in the past

    $random = (rand()%4);
    $messags = array("Hello!",
                     "Greetings",
                     "Welcome",
                     "Salute");

    print($messags[$random]);
    print(date(' H:m:s'));

?>
