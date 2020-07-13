package com.onyx.gallery.handler

import android.graphics.Path
import com.onyx.android.sdk.scribble.shape.ImageShape
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.gallery.views.ImageShapeExpand

/**
 * Created by Leung 2020/7/10 15:27
 **/
class UndoRedoHandler {
    private val shapeOperationHandler = OperationHandler<Shape>()
    private val mosaicOperationHandler = OperationHandler<Path>()

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

    fun addMosaic(path: Path) {
        mosaicOperationHandler.add(path)
    }

    fun getMocais(): MutableList<Path> = mosaicOperationHandler.getAllOperation()

    fun clearMosaic() {
        mosaicOperationHandler.clear()
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

    fun undoMosaic(): Path? {
        return mosaicOperationHandler.undo()
    }

    fun redoMosaic(): Path? {
        return mosaicOperationHandler.redo()
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

