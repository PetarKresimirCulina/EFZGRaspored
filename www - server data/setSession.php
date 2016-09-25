<?php
	header('Content-type: text/plain; charset=utf-8');
	
        function setSession()
	{
		if(!isset($_SESSION['database']))
		{
			$_SESSION['database'] = "some_database_name";
		}
	}
?>	