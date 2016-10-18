<?php
	
	session_start();
	
	if(empty($_SESSION['guid'])) {
		header('Location: ../index.php');
		exit();
	}
	
	$info = pathinfo($_FILES['fileUpload']['name']);
	$ext = $info['extension'];
	
	if($ext != "xml")
	{
		$_SESSION["error"] = "Pogrešan tip datoteke. Podržan je samo XML format.";
		header('Location: upload.php');
	}
	
	$newname = "raspored.".$ext; 
	
	$target = $newname;
	move_uploaded_file( $_FILES['fileUpload']['tmp_name'], $target);

	$_SESSION["xmlfile"] = $target;

	header('Location: /php/load.php');
	
?>