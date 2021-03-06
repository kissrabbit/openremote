/*
 * Copyright 2016, OpenRemote Inc.
 *
 * See the CONTRIBUTORS.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.manager.shared.ngsi;

import elemental.json.JsonArray;
import elemental.json.JsonObject;

import java.util.LinkedHashSet;
import java.util.Set;

public class KeyValueEntity extends AbstractEntity<KeyValueAttribute> {

    public static KeyValueEntity[] from(JsonArray jsonArray) {
        KeyValueEntity[] array = new KeyValueEntity[jsonArray.length()];
        for (int i = 0; i < array.length; i++) {
            array[i] = new KeyValueEntity(jsonArray.get(i));
        }
        return array;
    }

    public static KeyValueEntity from(JsonObject jsonObject) {
        return new KeyValueEntity(jsonObject);
    }

    public KeyValueEntity(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public KeyValueAttribute[] getAttributes() {
        Set<KeyValueAttribute> attributes = new LinkedHashSet<>();
        String[] keys = jsonObject.keys();
        for (String key : keys) {
            if (hasAttribute(key)) {
                KeyValueAttribute attribute = new KeyValueAttribute(key, jsonObject.get(key));
                attributes.add(attribute);
            }
        }
        return attributes.toArray(new KeyValueAttribute[attributes.size()]);
    }

    @Override
    public KeyValueAttribute getAttribute(String name) {
        return hasAttribute(name) ? new KeyValueAttribute(name, jsonObject.get(name)) : null;
    }

    @Override
    public KeyValueEntity addAttribute(KeyValueAttribute attribute) {
        jsonObject.put(attribute.getName(), attribute.getValue());
        return this;
    }

    @Override
    public KeyValueEntity removeAttribute(String name) {
        if (hasAttribute(name)) {
            jsonObject.remove(name);
        }
        return this;
    }

    @Override
    public void clearAttributes() {
        for (KeyValueAttribute attribute : getAttributes()) {
            removeAttribute(attribute.getName());
        }
    }

    @Override
    protected void validateAttributes(Set<ModelValidationError> errors) {
        for (KeyValueAttribute attribute : getAttributes()) {
            ModelProblem[] problems = Model.validateField(attribute.getName());
            for (ModelProblem problem : problems) {
                errors.add(new ModelValidationError("attributeName", problem));
            }
        }
    }

}
