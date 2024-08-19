package util

import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.TwoWayConverter


class DoubleConverter: TwoWayConverter<Double, AnimationVector1D> {
    override val convertFromVector: (AnimationVector1D) -> Double = { vector ->
        vector.value.toDouble()
    }
    override val convertToVector: (Double) -> AnimationVector1D ={
        AnimationVector1D(it.toFloat())
    }

}