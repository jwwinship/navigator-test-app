// Copyright (c) 2020 Facebook, Inc. and its affiliates.
// All rights reserved.
//
// This source code is licensed under the BSD-style license found in the
// LICENSE file in the root directory of this source tree.

package org.pytorch.demo.objectdetection;

import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * class Result
 * to store the object class index, detection score and the bounding box
 */
class Result {
    int classIndex;
    Float score;
    Rect rect;

    public Result(int cls, Float output, Rect rect) {
        this.classIndex = cls;
        this.score = output;
        this.rect = rect;
    }
};

/**
 * class PrePostProcessor
 * handle the result processing
 */
public class PrePostProcessor {
    // for yolov5 model, no need to apply MEAN and STD
    public final static float[] NO_MEAN_RGB = new float[] {0.0f, 0.0f, 0.0f};
    public final static float[] NO_STD_RGB = new float[] {1.0f, 1.0f, 1.0f};

    // model input image size
    public final static int INPUT_WIDTH = 640;
    public final static int INPUT_HEIGHT = 640;
    public final static int OUTPUT_COLUMN = 6; // left, top, right, bottom, score and label

    static String[] mClasses;
    static int[] classesToDetect = new int[]{0,61,68}; // 0:person, 61:chair, 68:potted plant

    // finalize the results
    static ArrayList<Result> outputsToPredictions(int countResult, float[] outputs, float imgScaleX, float imgScaleY, float ivScaleX, float ivScaleY, float startX, float startY) {
        ArrayList<Result> results = new ArrayList<>();
        for (int i = 0; i< countResult; i++) {

            int finalI = i;
            if (Arrays.stream(classesToDetect).noneMatch(n-> n==outputs[finalI*OUTPUT_COLUMN +5]))
                continue;
            /*if (outputs[i* OUTPUT_COLUMN +5] != 0 ) //TODO: This is where we decide which classes we want to detect.
                continue;*/

            float left = outputs[i* OUTPUT_COLUMN];
            float top = outputs[i* OUTPUT_COLUMN +1];
            float right = outputs[i* OUTPUT_COLUMN +2];
            float bottom = outputs[i* OUTPUT_COLUMN +3];

            left = imgScaleX * left;
            top = imgScaleY * top;
            right = imgScaleX * right;
            bottom = imgScaleY * bottom;

            /*float horShift = 50;//Boxes are drawn inaccurately, usually shifted significantly to the left. We need to manually adjust until we can figure out why boxes are shifted
            left += horShift;
            right += horShift;*/

            Rect rect = new Rect((int)(startX+ivScaleX*left), (int)(startY+top*ivScaleY), (int)(startX+ivScaleX*right), (int)(startY+ivScaleY*bottom));
            Result result = new Result((int)outputs[i* OUTPUT_COLUMN +5], outputs[i* OUTPUT_COLUMN +4], rect);
            results.add(result);

        }
        return results;
    }
}
