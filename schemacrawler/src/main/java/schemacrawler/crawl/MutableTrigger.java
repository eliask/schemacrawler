/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.crawl;


import schemacrawler.schema.*;

/**
 * Represents a trigger.
 *
 * @author Sualeh Fatehi
 */
class MutableTrigger
  extends AbstractDependantObject
  implements Trigger
{

  private static final long serialVersionUID = -1619291073229701764L;

  private EventManipulationType eventManipulationType;
  private int actionOrder;
  private final StringBuilder actionCondition;
  private final StringBuilder actionStatement;
  private ActionOrientationType actionOrientation;
  private ConditionTimingType conditionTiming;

  MutableTrigger(final Table parent, final String name)
  {
    super(parent, name);
    // Default values
    eventManipulationType = EventManipulationType.unknown;
    actionOrientation = ActionOrientationType.unknown;
    conditionTiming = ConditionTimingType.unknown;
    actionCondition = new StringBuilder();
    actionStatement = new StringBuilder();
  }

  /**
   * {@inheritDoc}
   */
  public String getActionCondition()
  {
    return actionCondition.toString();
  }

  /**
   * {@inheritDoc}
   */
  public int getActionOrder()
  {
    return actionOrder;
  }

  /**
   * {@inheritDoc}
   */
  public ActionOrientationType getActionOrientation()
  {
    return actionOrientation;
  }

  /**
   * {@inheritDoc}
   */
  public String getActionStatement()
  {
    return actionStatement.toString();
  }

  /**
   * {@inheritDoc}
   */
  public ConditionTimingType getConditionTiming()
  {
    return conditionTiming;
  }

  /**
   * {@inheritDoc}
   */
  public EventManipulationType getEventManipulationType()
  {
    return eventManipulationType;
  }

  void appendActionCondition(final String actionCondition)
  {
    if (actionCondition != null)
    {
      this.actionCondition
        .append(actionCondition);
    }
  }

  void appendActionStatement(final String actionStatement)
  {
    if (actionStatement != null)
    {
      this.actionStatement
        .append(actionStatement);
    }
  }

  void setActionOrder(final int actionOrder)
  {
    this.actionOrder = actionOrder;
  }

  void setActionOrientation(final ActionOrientationType actionOrientation)
  {
    this.actionOrientation = actionOrientation;
  }

  void setConditionTiming(final ConditionTimingType conditionTiming)
  {
    this.conditionTiming = conditionTiming;
  }

  void setEventManipulationType(final EventManipulationType eventManipulationType)
  {
    this.eventManipulationType = eventManipulationType;
  }

}
