<?php

$db_host="localhost";   
$db_user="thousand419";
$db_passwd=""; 
$db_name="thousand419";

$id = $_REQUEST["id"];


// MySQL - DB ����.

$conn = mysqli_connect($db_host,$db_user,$db_passwd,$db_name);

if (mysqli_connect_errno()){

echo "MySQL ���� ����: " . mysqli_connect_error();

exit;

} else {

echo "DB : \"$db_name\"�� ���� ����.<br/>";

}



// ���ڼ� ����, utf8.

mysqli_set_charset($conn,"utf8"); 



// ���̺� ���� �� ���� ���.

$sql = "SELECT * FROM info where id=$id";

if ($result = mysqli_query($conn,$sql)){

echo "<table border='1' cellpadding='5'> <tr nowrap='' bgcolor='#e0e0e0'> 

<th>ID</th> 

<th>x</th> 

<th>y</th> 

</tr>";



while($row = mysqli_fetch_array($result)){

echo "<tr>";

echo "<td nowrap=''>" . $row['id'] . "</td>";

echo "<td nowrap=''>" . $row['x'] . "</td>";

echo "<td nowrap=''>" . $row['y'] . "</td>";

echo "</tr>";

} 

echo "</table>";



mysqli_close($conn);



} else {

echo "���̺� ���� ����: " . mysqli_error($conn);

exit;

}

?>