function submitScheduleSearchForm()
{
	if(hasSpecialChar($("input[name='keywords']",$("#scheduleSearchForm")).val()))
	{
		alertMsg.error(DWZ.msg("alertHasSpecialChar"));
		return false;
	}
	$("#scheduleSearchForm").submit();
}