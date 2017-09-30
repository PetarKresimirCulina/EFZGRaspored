<?php
	include('../connection_db.php');
	$connection = connectDB();

	$program_id = intval($_POST['programid']);
	$year = intval($_POST['year']);
	
	//echo $program_id . ' BLAH ' . $year;
	getGroups($connection, $program_id, $year);

	$connection->close();

	function getGroups ($connection, $program_id, $year) //method 1
	{
		if($program_id == 1 && $year == 4)
		{
			$result = $connection->query("SELECT * FROM branches, groups WHERE groups.branch_id=branches.id AND branches.program_id=$program_id AND (branches.year=3 OR branches.year=4) AND EXISTS(SELECT * FROM courses WHERE courses.branch_id=branches.id) ORDER BY groups.name ASC");
			if($result->num_rows > 0)
			{
				while($row = mysqli_fetch_array($result))
				{
					echo $row['id'] . '&' . $row['name'] . '&' . $row['parent_id'] . '<>';
				}
			}
			else { return 0; }
		}
		else
		{
			$result = $connection->query("SELECT * FROM branches, groups WHERE groups.branch_id=branches.id AND branches.program_id=$program_id AND branches.year=$year AND EXISTS(SELECT * FROM courses WHERE courses.branch_id=branches.id) ORDER BY groups.name ASC");
			if($result->num_rows > 0)
			{
				while($row = mysqli_fetch_array($result))
				{
					echo $row['id'] . '&' . $row['name'] . '&' . $row['parent_id'] . '<>';
				}
			}
			else { return 0; }
		}
		
	}
?>