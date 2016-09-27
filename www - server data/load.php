<?php
	session_start();
	header('Content-type: text/plain; charset=utf-8');
	/* 	
		Program za učitavanje Wise Timetable XML datoteka u MySQL bazu podataka
		Copyright (c) 2016 Petar-Krešimir Čulina
		Verzija 1.0 - 26/02/2016
	*/
	
	
	
	include'connection_db.php';
	include 'loadTimeSlots.php';
	include 'loadRoomProperties.php';
	include 'loadExecutionTypes.php';
	include 'loadRooms.php';
	include 'loadTutors.php';
	include 'loadPrograms.php';
	include 'loadBranches.php';
	include 'loadGroups.php';
	include 'loadCourses.php';
	include 'loadSchedules.php';
	
	
	$start = time();

	$tmpFile = $_SESSION["xmlfile"];
	
	if(empty($tmpFile))
	{
		die ("XML datoteka je prazna.");
	}
	
	echo "\n\r" . 'Loading XML file...';
	$xml = simplexml_load_file($tmpFile);
	echo "\n\r" . 'XML File loaded: ' . tmpFile();
	
	
	$connection = connectDB();
	
	loadTimeSlots($xml, $connection);
	loadRoomProperties($xml, $connection);
	loadExecutionTypes($xml, $connection);
	loadRooms($xml, $connection);
	loadTutors($xml, $connection);
	loadPrograms($xml, $connection);
	loadBranches($xml, $connection);
	loadGroups($xml, $connection);
	loadCourses($xml, $connection);
	loadSchedules($xml, $connection);
	
	echo "\n\r" . 'Completed';
	
	echo "\n\r" . 'Closing MySQL connection...';
	$connection->close();
	echo "\n\r" . 'Connection closed';
	
	$end = time();
	$diff = ($end - $start);
	
	echo "\n\r" . 'Finished in ' . $diff . 's';
	
?>