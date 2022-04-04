/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.aggregation.Aggregatable
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import javax.naming.OperationNotSupportedException

class PivotTapbe {
    lateinit var data : DataFrame<String>
    lateinit var  grouping: Grouping
    lateinit var  funct : Function

    fun apply() {
        val k = if (grouping.list1.isEmpty() && grouping.list2.isEmpty()) {
            data
        } else if (grouping.list2.isEmpty()) {
            pivot(grouping.list1.size)
        } else if (grouping.list1.isEmpty()) {
            groupBy(grouping.list2.size, data)
        } else {
            groupBy(grouping.list2.size, pivot(grouping.list1.size))
        }

        val p = when(funct){
            Function.MAX -> k._max()
            Function.MEAN -> k._mean()
            Function.SUM ->k._sum()
            Function.MIN ->k._min()
            else -> TODO()
        }
    }

    fun pivot(l1: Int) : Pivot<String> {
        return if (l1 == 1) {
            data.pivot {
                grouping.list1[0]
            }
        }
        else {
            data.pivot {
                grouping.list1.subList(2, grouping.list1.size).fold(grouping.list1[0] then grouping.list1[1]) { a, b ->
                    a then b
                }
            }
        }
    }

    fun groupBy( l2: Int , t : DataFrame<*>): GroupBy<Any?, Any?> {
        return if (l2 == 1) {
            t.groupBy{
                grouping.list2[0]
            }
        }
        else {
            t.groupBy{
                grouping.list2.subList(2, grouping.list2.size).fold(grouping.list2[0] and grouping.list2[1]) { a , b ->
                    a and b
                }
            }
        }
    }

    fun groupBy( l2: Int , t : Pivot<*>): PivotGroupBy<Any?> {
        return if (l2 == 1) {
            t.groupBy{
                grouping.list2[0]
            }
        }
        else {
            t.groupBy{
                grouping.list2.subList(2, grouping.list2.size).fold(grouping.list2[0] and grouping.list2[1]) { a , b ->
                    a and b
                }
            }
        }
    }

    fun Aggregatable<*>._max(): DataRow<*> {
        return if(this is Pivot<*>) {
            this.max()
        } else if(this is DataFrame<*>) {
            this.max()
        } else {
            throw OperationNotSupportedException()
        }
    }
    fun Aggregatable<*>._mean(): DataRow<*> {
        return if(this is Pivot<*>) {
            this.mean()
        } else if(this is DataFrame<*>) {
            this.mean()
        } else {
            throw OperationNotSupportedException()
        }
    }
    fun Aggregatable<*>._sum(): DataRow<*> {
        return if(this is Pivot<*>) {
            this.sum()
        } else if(this is DataFrame<*>) {
            this.sum()
        } else {
            throw OperationNotSupportedException()
        }
    }
    fun Aggregatable<*>._min(): DataRow<*> {
        return if(this is Pivot<*>) {
            this.min()
        } else if(this is DataFrame<*>) {
            this.min()
        } else {
            throw OperationNotSupportedException()
        }
    }

    fun getValueAt(){
        //retourner la valeur à affciher dans (row,col)
    }
}

enum class Function{
    SUM,
    MEAN,
    MIN,
    MAX
}
class Grouping{
    var list1 = mutableListOf<ColumnSet<*>>()
    var list2 = mutableListOf<ColumnSet<*>>()
}
