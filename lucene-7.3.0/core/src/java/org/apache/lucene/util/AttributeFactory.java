/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lucene.util;


import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * An AttributeFactory creates instances of {@link AttributeImpl}s.
 */
public abstract class AttributeFactory {
  
  /**
   * Returns an {@link AttributeImpl} for the supplied {@link Attribute} interface class.
   * 
   * @throws UndeclaredThrowableException A wrapper runtime exception thrown if the 
   *         constructor of the attribute class throws a checked exception. 
   *         Note that attributes should not throw or declare 
   *         checked exceptions; this may be verified and fail early in the future. 
   */
  public abstract AttributeImpl createAttributeInstance(Class<? extends Attribute> attClass)
      throws UndeclaredThrowableException;
  
  /**
   * Returns a correctly typed {@link MethodHandle} for the no-arg ctor of the given class.
   */
  static final MethodHandle findAttributeImplCtor(Class<? extends AttributeImpl> clazz) {
    try {
      org.apache.lucene.LucenePackage.writeLog("AttributeFactory.findAttributeImplCtor() - 1: " + lookup.findConstructor(clazz, NO_ARG_CTOR).toString());
      MethodHandle mh = lookup.findConstructor(clazz, NO_ARG_CTOR).asType(NO_ARG_RETURNING_ATTRIBUTEIMPL);
      org.apache.lucene.LucenePackage.writeLog("AttributeFactory.findAttributeImplCtor() - 2: " + mh.toString());
      return mh;
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new IllegalArgumentException("Cannot lookup accessible no-arg constructor for: " + clazz.getName(), e);
    }
  }
  
  private static final MethodHandles.Lookup lookup = MethodHandles.publicLookup();
  private static final MethodType NO_ARG_CTOR = MethodType.methodType(void.class);
  private static final MethodType NO_ARG_RETURNING_ATTRIBUTEIMPL = MethodType.methodType(AttributeImpl.class);
  
  /**
   * This is the default factory that creates {@link AttributeImpl}s using the
   * class name of the supplied {@link Attribute} interface class by appending <code>Impl</code> to it.
   */
  public static final AttributeFactory DEFAULT_ATTRIBUTE_FACTORY = new DefaultAttributeFactory();
  
  private static final class DefaultAttributeFactory extends AttributeFactory {
    /*
    private final Class__Value<MethodHandle> constructors = new Class__Value<MethodHandle>() {
      @Override
      protected MethodHandle computeValue(Class<?> attClass) {
        return findAttributeImplCtor(findImplClass(attClass.asSubclass(Attribute.class)));
      }
    };
    */

    private final org.apache.lucene.util.ADClassValue<MethodHandle> constructors = new org.apache.lucene.util.ADClassValue<MethodHandle>() {
      @Override
      protected MethodHandle computeValue(Class<?> attClass) {
        Class<? extends Attribute> attClass2 = attClass.asSubclass(Attribute.class);
        org.apache.lucene.LucenePackage.writeLog("AttributeFactory.constructors.computeValue() - 1: " + attClass2.getName());
        Class<? extends AttributeImpl> attClass3 = findImplClass(attClass2); 
        org.apache.lucene.LucenePackage.writeLog("AttributeFactory.constructors.computeValue() - 2: " + attClass3.getName());
        MethodHandle mh = findAttributeImplCtor(attClass3);
        org.apache.lucene.LucenePackage.writeLog("AttributeFactory.constructors.computeValue() - 3: " + mh.toString());
        return mh;
      }
    };

    DefaultAttributeFactory() {}

    @Override
    public AttributeImpl createAttributeInstance(Class<? extends Attribute> attClass) {
      try {
        org.apache.lucene.LucenePackage.writeLog("AttributeFactory.createAttributeInstance() - 1: " + attClass.getName());

        MethodHandle mh = constructors.get(attClass);

        org.apache.lucene.LucenePackage.writeLog("AttributeFactory.createAttributeInstance() - 2: " + attClass.getName());
        return (AttributeImpl) mh.invokeExact();

        //return (AttributeImpl) constructors.get(attClass).invokeExact();
      } catch (Error | RuntimeException e) {
        throw e;
      } catch (Throwable e) {
        throw new UndeclaredThrowableException(e);
      }
    }

    private Class<? extends AttributeImpl> findImplClass(Class<? extends Attribute> attClass) {
      try {
        return Class.forName(attClass.getName() + "Impl", true, attClass.getClassLoader()).asSubclass(AttributeImpl.class);
      } catch (ClassNotFoundException cnfe) {
        throw new IllegalArgumentException("Cannot find implementing class for: " + attClass.getName());
      }      
    }
  }
  
  /** <b>Expert</b>: AttributeFactory returning an instance of the given {@code clazz} for the
   * attributes it implements. For all other attributes it calls the given delegate factory
   * as fallback. This class can be used to prefer a specific {@code AttributeImpl} which
   * combines multiple attributes over separate classes.
   * @lucene.internal
   */
  public abstract static class StaticImplementationAttributeFactory<A extends AttributeImpl> extends AttributeFactory {
    private final AttributeFactory delegate;
    private final Class<A> clazz;
    
    /** <b>Expert</b>: Creates an AttributeFactory returning {@code clazz} as instance for the
     * attributes it implements and for all other attributes calls the given delegate factory. */
    public StaticImplementationAttributeFactory(AttributeFactory delegate, Class<A> clazz) {
      this.delegate = delegate;
      this.clazz = clazz;
    }
    
    @Override
    public final AttributeImpl createAttributeInstance(Class<? extends Attribute> attClass) {
      return attClass.isAssignableFrom(clazz) ? createInstance() : delegate.createAttributeInstance(attClass);
      //return delegate.createAttributeInstance(attClass);
    }
    
    /** Creates an instance of {@code A}. */
    protected abstract A createInstance();
    
    @Override
    public boolean equals(Object other) {
      if (this == other)
        return true;
      if (other == null || other.getClass() != this.getClass())
        return false;
      @SuppressWarnings("rawtypes")
      final StaticImplementationAttributeFactory af = (StaticImplementationAttributeFactory) other;
      return this.delegate.equals(af.delegate) && this.clazz == af.clazz;
    }
    
    @Override
    public int hashCode() {
      return 31 * delegate.hashCode() + clazz.hashCode();
    }
  }
  
  /** Returns an AttributeFactory returning an instance of the given {@code clazz} for the
   * attributes it implements. The given {@code clazz} must have a public no-arg constructor.
   * For all other attributes it calls the given delegate factory as fallback.
   * This method can be used to prefer a specific {@code AttributeImpl} which combines
   * multiple attributes over separate classes.
   * <p>Please save instances created by this method in a static final field, because
   * on each call, this does reflection for creating a {@link MethodHandle}.
   */
  public static <A extends AttributeImpl> AttributeFactory getStaticImplementation(AttributeFactory delegate, Class<A> clazz) {
    final MethodHandle constr = findAttributeImplCtor(clazz);
    return new StaticImplementationAttributeFactory<A>(delegate, clazz) {
      @Override
      protected A createInstance() {
        try {
          return (A) constr.invokeExact();
        } catch (Error | RuntimeException e) {
          org.apache.lucene.LucenePackage.writeLog("StaticImplementationAttributeFactory<A>.createInstance() - EC: ");
          org.apache.lucene.LucenePackage.writeLog(e);
          throw e;
        } catch (Throwable e) {
          org.apache.lucene.LucenePackage.writeLog("StaticImplementationAttributeFactory<A>.createInstance() - EC: ");
          org.apache.lucene.LucenePackage.writeLog(e);
          throw new UndeclaredThrowableException(e);
        }
      }
    };
  }
}