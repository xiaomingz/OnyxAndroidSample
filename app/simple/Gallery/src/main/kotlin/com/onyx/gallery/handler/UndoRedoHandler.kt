package com.onyx.gallery.handler

import com.onyx.android.sdk.scribble.shape.ImageShape
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.android.sdk.utils.FileUtils
import com.onyx.gallery.models.CropSnapshot
import com.onyx.gallery.views.shape.ImageShapeExpand

/**
 * Created by Leung 2020/7/10 15:27
 **/
class UndoRedoHandler {
    private val shapeOperationHandler = OperationHandler<Shape>()
    private val cropSnapshotList = mutableListOf<CropSnapshot>()
    private var currCropSnapshotIndex = -1

    fun addShape(shape: Shape) {
        shapeOperationHandler.add(shape)
    }

    fun addShapes(shapes: MutableList<Shape>) {
        shapeOperationHandler.addAll(shapes)
    }

    fun getShapes(): MutableList<Shape> = shapeOperationHandler.getAllOperation()

    fun clearShapes() {
        shapeOperationHandler.clear()
    }

    fun undoShapes(): Shape? {
        val prepareUndoOperation = shapeOperationHandler.getPrepareUndoOperation()
        if (prepareUndoOperation is ImageShape || prepareUndoOperation is ImageShapeExpand) {
            return null
        }
        return shapeOperationHandler.undo()
    }

    fun redoShapes(): Shape? {
        return shapeOperationHandler.redo()
    }

    fun eraseShapes(removeShapes: List<Shape>) {
        shapeOperationHandler.undoList(removeShapes)
    }

    fun addCropSnapshot(cropSnapshot: CropSnapshot) {
        currCropSnapshotIndex += 1
        cropSnapshotList.add(currCropSnapshotIndex, cropSnapshot)
    }

    fun undoCrop(): CropSnapshot? {
        currCropSnapshotIndex -= 1
        if (currCropSnapshotIndex < 0) {
            currCropSnapshotIndex = 0
        }
        return cropSnapshotList[currCropSnapshotIndex]
    }

    fun redoCrop(): CropSnapshot? {
        currCropSnapshotIndex += 1
        if (currCropSnapshotIndex > cropSnapshotList.size - 1) {
            currCropSnapshotIndex = cropSnapshotList.size - 1
        }
        return cropSnapshotList[currCropSnapshotIndex]
    }

    fun cleardCropSnapshot() {
        cropSnapshotList.forEachIndexed { index, cropSnapshot ->
            if (index != 0) {
                FileUtils.deleteFile(cropSnapshot.imagePath)
            }
        }
        currCropSnapshotIndex = -1
        cropSnapshotList.clear()
    }

    fun getCurrCropSnapshot(): CropSnapshot {
        if (cropSnapshotList.isEmpty() || currCropSnapshotIndex < 0) {
            throw RuntimeException("load image finsh mast be makeCropSnapshot")
        }
        if (currCropSnapshotIndex > cropSnapshotList.size - 1) {
            throw RuntimeException("current cropSnapshot index error")
        }
        return cropSnapshotList[currCropSnapshotIndex]
    }

    fun hasCropModify(): Boolean {
        return cropSnapshotList.size > 1 && currCropSnapshotIndex > 0
    }
}

class OperationHandler<T> {
    private val operations = mutableListOf<T>()
    private val redoOperations = mutableListOf<T>()
    private val undoOperations = mutableListOf<T>()

    fun add(operation: T) {
        if (!redoOperations.isEmpty()) {
            redoOperations.add(0, operation)
        }
        operations.add(operation)
    }

    fun addAll(operations: MutableList<T>) {
        if (!redoOperations.isEmpty()) {
            redoOperations.addAll(0, operations)
        }
        this.operations.addAll(operations)
    }

    fun getAllOperation(): MutableList<T> {
        return operations
    }

    fun getPrepareUndoOperation(): T {
        return operations.get(operations.size - 1)
    }

    fun undo(): T? {
        if (canUndo()) {
            val operation = operations.removeAt(operations.size - 1)
            redoOperations.add(operation)
            return operation
        }
        return null
    }

    fun undoList(removeOperationList: List<T>) {
        for (operation in removeOperationList) {
            val indexOf = operations.indexOf(operation)
            if (indexOf < 0) {
                continue
            }
            val removeOperation = operations.removeAt(indexOf)
            redoOperations.add(removeOperation)
        }
    }

    fun redo(): T? {
        if (canRedo()) {
            val operation = redoOperations.removeAt(redoOperations.size - 1)
            operations.add(operation)
            undoOperations.add(operation)
            return operation
        }
        return null
    }

    private fun canUndo(): Boolean {
        return !operations.isEmpty()
    }

    private fun canRedo(): Boolean {
        return !redoOperations.isEmpty()
    }

    fun clear() {
        operations.clear()
        undoOperations.clear()
        redoOperations.clear()
    }


}

