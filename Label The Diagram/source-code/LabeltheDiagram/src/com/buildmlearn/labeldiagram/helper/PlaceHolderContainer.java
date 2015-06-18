package com.buildmlearn.labeldiagram.helper;

import java.util.ArrayList;
import java.util.List;

import com.example.labelthediagram.R;



/**
 * @author Akilaz
 * This helper class is for separating the place holders which are on
 * the left side and right side of a particular diagram in order to 
 * determine the tag dropping behavior in the DiagramPlay Activity
 */
public class PlaceHolderContainer {


	private Integer[] leftPlaceHolderList;
	private Integer[] rightPlaceHolderList;
	private List<Integer[]> listHolder;

	public PlaceHolderContainer() {
		
		this.leftPlaceHolderList=new Integer[]{};
		this.rightPlaceHolderList=new Integer[]{};
		this.listHolder=new ArrayList<Integer[]>();

	}

	// Switching the helper method according to the diagram name
	public List<Integer[]> diagramCaller(String diagramName) {
		if (!diagramName.equals(null)) {
			switch (diagramName) {

				case "HumanEye":
					getHumanEyeMarkerList();
					break;
				case "HumanHeart":
					getHumanHeartMarkerList();
					break;
				case "HumanEar":
					getHumanEarMarkerList();
					break;
				// TODO The other diagrams goes here

			}
			return listHolder;
		}else {
			return null;
		}
	}

	private List<Integer[]> getHumanHeartMarkerList() {

		leftPlaceHolderList = new Integer[] { 
				R.id.sup_vena_cavaBlb, R.id.right_atriumBlb,
				R.id.inf_vena_cavaBlb
		};
		
		rightPlaceHolderList = new Integer[]{
				R.id.right_ventricleBlb,R.id.left_ventricleBlb,R.id.left_atriumBlb,
				R.id.pul_arteryBulb,R.id.aortaBlb,R.id.pul_veinBlb,
		};
		
		listHolder.add(leftPlaceHolderList);
		listHolder.add(rightPlaceHolderList);
		
		return listHolder;
		
	}

	/*
	 * Return list of arrays containing left placeholder Ids and right placeholder Ids
	 * of HumanEar diagram
	 */
	private List<Integer[]> getHumanEarMarkerList() {
		
		leftPlaceHolderList = new Integer[] { 
				R.id.pinnaBlb, R.id.malleusBlb,
				R.id.outerearBlb
		};
		
		rightPlaceHolderList = new Integer[]{
				R.id.earcanalBlb,R.id.eardrumBlb,R.id.cochleaBlb,
				R.id.earnerveBlb,R.id.incusBlb,R.id.stapesBlb,
				R.id.canalsBlb
		};
		
		listHolder.add(leftPlaceHolderList);
		listHolder.add(rightPlaceHolderList);
		
		return listHolder;
	}

	/*
	 * Return list of arrays containing left placeholder Ids and right placeholder Ids
	 * of HumanEye diagram
	 */
	private List<Integer[]> getHumanEyeMarkerList() {

		leftPlaceHolderList = new Integer[] { 
				R.id.corneaBlb, R.id.ciliaryBodyBlb,
				R.id.pul_arteryBulb,R.id.pupilBlb,R.id.lensBlb,R.id.vitreousBlb
		};
		
		rightPlaceHolderList = new Integer[]{
				R.id.foveaBlb,R.id.retinaBlb,R.id.opticNerveBlb,R.id.blindSpotBlb
		};
		
		listHolder.add(leftPlaceHolderList);
		listHolder.add(rightPlaceHolderList);
		
		return listHolder;

	}

}
