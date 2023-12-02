package com.tehang.common.utility.baseclass;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 作为所有聚合根的基类。
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings( {"PMD.GenericsNaming", "PMD.AbstractClassWithoutAnyMethod"})
public abstract class AggregateRootBase<PK extends Serializable> extends EntityObject {

}
