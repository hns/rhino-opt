package org.mozilla.javascript.optimizer;

import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public abstract class OptCall extends ScriptableObject {

    public Object arguments;

    public OptCall(NativeFunction function, Scriptable parentScope, Object[] args) {
        setParentScope(parentScope);
        this.arguments = new OptArguments(function, args);
    }

    protected abstract Object getArgument(int index);
    protected abstract void putArgument(int index, Object value);

    @Override
    public String getClassName() {
        return "Call";
    }

    class OptArguments extends ScriptableObject {

        NativeFunction function;
        Object[] args;
        int length;
        Integer lengthObj;
        Object constructor;

        OptArguments(NativeFunction function, Object[] args) {
            this.function = function;
            this.args = args;
            this.length = args.length;
            this.lengthObj = Integer.valueOf(this.length);
            this.constructor = ScriptableObject.getTopScopeValue(this, "Object");
            Scriptable parent = OptCall.this.getParentScope();
            setParentScope(parent);
            setPrototype(ScriptableObject.getObjectPrototype(parent));            
        }

        public String getClassName() {
            return "Arguments";
        }

        @Override
        public Object get(String name, Scriptable start) {
            if ("length".equals(name)) {
                return lengthObj;
            } else if ("constructor".equals(name)) {
                return constructor;
            } else if ("callee".equals(name)) {
                return function;
            }
            return super.get(name, start);
        }

        @Override
        public Object get(int index, Scriptable start) {
            if (index < 0 || index >= length) {
                return super.get(index, start);
            }
            return args[index];
        }

        @Override
        public void put(int index, Scriptable start, Object value) {
            if (index < 0 || index >= length) {
                super.put(index, start, value);
            }
            putArgument(index, value);
        }

        @Override
        public boolean has(int index, Scriptable start) {
            return super.has(index, start) || (index >= 0 && index < length);
        }

        @Override
        public Object[] getIds() {
            Object[] ids = new Object[length];
            for (int i = 0; i < length; i++) {
                ids[i] = Integer.valueOf(i);
            }
            return ids;
        }
    }

}

