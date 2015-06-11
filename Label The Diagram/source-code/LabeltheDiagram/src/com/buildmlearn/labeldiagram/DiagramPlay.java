package com.buildmlearn.labeldiagram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.buildmlearn.labeldiagram.resources.PlaceHolderContainer;
import com.buildmlearn.labeldiagram.resources.TagPlaceholderMapper;
import com.example.labelthediagram.R;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.DragEvent;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DiagramPlay extends Activity implements OnDragListener,
		OnLongClickListener {

	private static final String TAG = "InfoTag";
	private static final String TAG_ERROR = "Error";
	/*
	 * private static final List<ImageView> leftPlaceHolderList = new
	 * ArrayList<ImageView>(); private static final List<ImageView>
	 * rightPlaceHolderList = new ArrayList<ImageView>();
	 */
	List<Integer[]> placeHolderlist = new ArrayList<Integer[]>();
	SparseIntArray tagPlaceHolderMap = new SparseIntArray();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.diagram_play);

		// Score board textViews
		TextView completeTxt = (TextView) findViewById(R.id.complatedTxt);
		TextView compeleteRatio = (TextView) findViewById(R.id.complete_ratio);
		TextView scoreTxt = (TextView) findViewById(R.id.scoreTxt);
		TextView score = (TextView) findViewById(R.id.score);

		Typeface tfThin = Typeface.createFromAsset(getAssets(),
				"fonts/Roboto-Thin.ttf");
		Typeface tfLight = Typeface.createFromAsset(getAssets(),
				"fonts/Roboto-Light.ttf");

		// Setting up font face to Roboto Light/Thin
		completeTxt.setTypeface(tfThin);
		scoreTxt.setTypeface(tfThin);
		compeleteRatio.setTypeface(tfThin);
		score.setTypeface(tfThin);

		// Placeholder imageViews
		ImageView irisView = (ImageView) findViewById(R.id.irisBlb);
		ImageView pupilView = (ImageView) findViewById(R.id.pupilBlb);
		ImageView lensView = (ImageView) findViewById(R.id.lensBlb);
		ImageView corneaView = (ImageView) findViewById(R.id.corneaBlb);
		ImageView vitreousView = (ImageView) findViewById(R.id.vitreousBlb);
		ImageView ciliaryView = (ImageView) findViewById(R.id.ciliaryBodyBlb);
		ImageView nerveView = (ImageView) findViewById(R.id.opticNerveBlb);
		ImageView blindspotView = (ImageView) findViewById(R.id.blindSpotBlb);
		ImageView foveaView = (ImageView) findViewById(R.id.foveaBlb);
		ImageView retinaView = (ImageView) findViewById(R.id.retinaBlb);

		// Draggable Tags imageViews
		TextView irisTag = (TextView) findViewById(R.id.irisTag);
		TextView pupilTag = (TextView) findViewById(R.id.pupilTag);
		TextView lensTag = (TextView) findViewById(R.id.lensTag);
		TextView corneaTag = (TextView) findViewById(R.id.corneaTag);
		TextView vitreousTag = (TextView) findViewById(R.id.vitreousTag);
		TextView ciliaryTag = (TextView) findViewById(R.id.ciliaryTag);
		TextView nerveTag = (TextView) findViewById(R.id.nerveTag);
		TextView blindspotTag = (TextView) findViewById(R.id.opticdiskTag);
		TextView foveaTag = (TextView) findViewById(R.id.foveaTag);
		TextView retinaTag = (TextView) findViewById(R.id.retinaTag);

		// Register draggable views to receive drag events
		irisView.setOnDragListener(this);
		pupilView.setOnDragListener(this);
		lensView.setOnDragListener(this);
		corneaView.setOnDragListener(this);
		vitreousView.setOnDragListener(this);
		ciliaryView.setOnDragListener(this);
		nerveView.setOnDragListener(this);
		blindspotView.setOnDragListener(this);
		foveaView.setOnDragListener(this);
		retinaView.setOnDragListener(this);

		// Register place holders to receive onlongclick events
		irisTag.setOnLongClickListener(this);
		pupilTag.setOnLongClickListener(this);
		lensTag.setOnLongClickListener(this);
		corneaTag.setOnLongClickListener(this);
		vitreousTag.setOnLongClickListener(this);
		ciliaryTag.setOnLongClickListener(this);
		nerveTag.setOnLongClickListener(this);
		blindspotTag.setOnLongClickListener(this);
		foveaTag.setOnLongClickListener(this);
		retinaTag.setOnLongClickListener(this);

		PlaceHolderContainer container = new PlaceHolderContainer();
		placeHolderlist = container.diagramCaller("HumanEye");
		
		TagPlaceholderMapper tagPlaceholdermapper= new TagPlaceholderMapper();
		tagPlaceHolderMap=tagPlaceholdermapper.diagramMapper("HumanEye");
	}

	@Override
	public boolean onLongClick(View textview) {
		ClipData clipData = ClipData.newPlainText("", "");

		View.DragShadowBuilder shadowBuilder = new TagDragShadowBuilder(
				textview);

		// Start the drag - contains the data to be dragged, metadata for this
		// data and callback for drawing shadow

		textview.startDrag(clipData, shadowBuilder, textview, 0);
		// Dragging the shadow so make the view invisible
		textview.setVisibility(View.INVISIBLE);
		return true;
	}

	@Override
	public boolean onDrag(View droppableView, DragEvent event) {
		// Defines a variable to store the action type for the incoming event
		final int action = event.getAction();
		View draggedImageTag = (View) event.getLocalState();

		// Handles each of the expected events
		switch (action) {

		case DragEvent.ACTION_DRAG_STARTED:

			// Determines if this View can accept the dragged data
			if (event.getClipDescription().hasMimeType(
					ClipDescription.MIMETYPE_TEXT_PLAIN)) {
		
				Log.i(TAG, "Can accept this data");

                // returns true to indicate that the View can accept the dragged data.
                return true;

            } else {
                Log.i(TAG, "Can not accept this data");

            }

			// Returns false. During the current drag and drop operation, this
			// View will
			// not receive events again until ACTION_DRAG_ENDED is sent.
			return false;

		case DragEvent.ACTION_DRAG_ENTERED:

			Toast.makeText(getApplicationContext(), "Ready to drop !",
					Toast.LENGTH_SHORT).show();
			Log.i(TAG, "drag action entered");
			return true;

		case DragEvent.ACTION_DRAG_LOCATION:
			Log.i(TAG, "drag action location");
			// Ignore the event
			return true;

		case DragEvent.ACTION_DRAG_EXITED:
			Log.i(TAG, "drag action exited");
			// Calls when the Tag exited the place holder imageView
			return true;

		case DragEvent.ACTION_DROP:

			boolean side;
			Log.i(TAG, "drag action dropped");
			
			// Set place holder imageViews invisible
			droppableView.setVisibility(View.INVISIBLE);
			
			// Get the positioning side(within diagram) of the currently entered place holder 
			side = getPlaceHolderSide(droppableView);

			// If placeholder is on left side, set XY coordinates of the draggedImageTag 
			// to right top corner
			if (side == true) {
				draggedImageTag.setX(droppableView.getX() + droppableView.getWidth()
								- draggedImageTag.getWidth());
				draggedImageTag.setY(droppableView.getY());
			} 
			// If placeholder is on right side, set XY coordinates of the draggedImageTag 
			// to left top corner
			else if (side == false) {
				draggedImageTag.setX(droppableView.getX());
				draggedImageTag.setY(droppableView.getY());
			}
						
			// Obtain the correct Tag id corresponding to place holder id
			int desiredTagId=tagPlaceHolderMap.get(droppableView.getId());
			
			//Get Tag id of currently moving tag
			int currentTagId = draggedImageTag.getId();
			
			//If currently moving tag id doesn't match actual tag id change the tag color to RED
			if(currentTagId!=desiredTagId){
				draggedImageTag.setBackgroundResource(R.drawable.custom_textview_incorrect);
			}
			//If currently moving tag id does match actual tag id change the tag color to light green
			else if(currentTagId==desiredTagId){
				draggedImageTag.setBackgroundResource(R.drawable.custom_textview_correct);
			}
			
			draggedImageTag.setVisibility(View.VISIBLE);
			return true;

		case DragEvent.ACTION_DRAG_ENDED:

			// Turns off any color tinting
			((ImageView) droppableView).clearColorFilter();

			// Invalidates the view to force a redraw
			droppableView.invalidate();

			// Does a getResult(), and displays what happened.
			if (event.getResult()) {
				Toast.makeText(this, "The drop was handled.", Toast.LENGTH_LONG);

			} else {
				Toast.makeText(this, "The drop didn't work.", Toast.LENGTH_LONG);

			}

			// returns true; the value is ignored.
			return true;

			// An unknown action type was received.
		default:
			Log.e("DragDrop Example",
					"Unknown action type received by OnDragListener.");
			break;
		}

		return false;

	}

	/*
	 * Indicate whether the placeholder is on left side or right side of the
	 * diagram
	 */
	private boolean getPlaceHolderSide(View droppableView) {

		boolean isOnLeftSide = false;

		// Check whether the place holder(droppableView) is in left side
		if (Arrays.asList(placeHolderlist.get(0)).contains(
				(Integer) droppableView.getId())) {

			isOnLeftSide = true;

		}
		// Check whether the place holder(droppableView) is in right side
		else if (Arrays.asList(placeHolderlist.get(1)).contains(
				droppableView.getId())) {

			isOnLeftSide = false;

		} else {
			Log.e(TAG_ERROR,
					"Position is undefined of the place holder imageView !");
		}

		return isOnLeftSide;
	}

	private static class TagDragShadowBuilder extends View.DragShadowBuilder {

		// Defines the constructor for TagDragShadowBuilder
		public TagDragShadowBuilder(View v) {

			// Stores the View parameter passed to TagDragShadowBuilder.
			super(v);

		}

		// Defines a callback that sends the drag shadow dimensions and touch
		// point back to the
		// system.
		@Override
		public void onProvideShadowMetrics(Point size, Point touch) {
			// Defines local variables
			int width;
			int height;

			// Sets the width of the shadow with respect to original View
			width = (getView().getWidth()) * 3;

			// Sets the height of the shadow with respect to original View
			height = (getView().getHeight()) * 3;

			// Sets the size parameter's width and height values. These get back
			// to the system through the size parameter.
			size.set(width, height);

			// Sets the touch point's position to the middle point visual
			// clarity
			touch.set((width / 4), (height / 4));
		}

		// Defines a callback that draws the drag shadow in a Canvas that the
		// system constructs
		// from the dimensions passed in onProvideShadowMetrics().
		@Override
		public void onDrawShadow(Canvas canvas) {

			// Draws the ColorDrawable in the Canvas passed in from the system.
			// Scale the canvas 1.5x
			canvas.scale(1.5f, 1.5f);
			getView().draw(canvas);
		}
	}

}
