<?php
header("Content-Type: text/html; charset=UTF-8");

$db_host="localhost";   
$db_user="thousand419";
$db_passwd=""; 
$db_name="thousand419";

$id = $_REQUEST["id"];
$x = $_REQUEST["x"];
$y = $_REQUEST["y"];

echo $id;
echo $x;
echo $y;

$conn = mysqli_connect($db_host, $db_user, $db_passwd, $db_name);
mysqli_query($conn, "set session character_set_connection=utf8;");
mysqli_query($conn, "set session character_set_results=utf8;");
mysqli_query($conn, "set session character_set_client=utf8;");
$query = "INSERT INTO info (id, x,y) VALUES ('$id', '$x','$y');";
$result = mysqli_query($conn, $query);

if($result)
      echo " |Result: OK";
    else
      echo " |Result: Error";

mysqli_close($conn);
?>