package edu.nu.jam.capstone.Interfaces;

import java.util.List;

import edu.nu.jam.capstone.Data.CommentData;

public interface ICommentBoardOperations
{
	List<CommentData> onGetComments();
	void onItemSelected(int position);
	void onTextViewClicked(int position);
	void onReplyClicked(int position);
	void onUpVoteClicked(int position);
}